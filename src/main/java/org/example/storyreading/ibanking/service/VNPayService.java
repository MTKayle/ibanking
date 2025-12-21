package org.example.storyreading.ibanking.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.storyreading.ibanking.config.VNPayConfig;
import org.example.storyreading.ibanking.dto.VNPayDepositRequest;
import org.example.storyreading.ibanking.dto.VNPayDepositResponse;
import org.example.storyreading.ibanking.dto.VNPayCallbackResponse;
import org.example.storyreading.ibanking.entity.CheckingAccount;
import org.example.storyreading.ibanking.entity.Transaction;
import org.example.storyreading.ibanking.entity.User;
import org.example.storyreading.ibanking.repository.CheckingAccountRepository;
import org.example.storyreading.ibanking.repository.TransactionRepository;
import org.example.storyreading.ibanking.repository.UserRepository;
import org.example.storyreading.ibanking.utils.VNPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VNPayService {

    @Autowired
    private VNPayConfig vnPayConfig;

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Tạo URL thanh toán VNPay để nạp tiền vào tài khoản
     */
    @Transactional
    public VNPayDepositResponse createPaymentUrl(Long userId, VNPayDepositRequest request, HttpServletRequest httpRequest) {
        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Tìm tài khoản checking đầu tiên của user
        CheckingAccount checkingAccount = checkingAccountRepository.findFirstByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản checking"));

        String accountNumber = checkingAccount.getAccount().getAccountNumber();

        // Tạo mã giao dịch duy nhất
        String txnRef = VNPayUtil.getRandomNumber(8) + System.currentTimeMillis();

        // Tạo các tham số cho VNPay
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnPayConfig.getVersion());
        vnpParams.put("vnp_Command", vnPayConfig.getCommand());
        vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnpParams.put("vnp_Amount", String.valueOf(request.getAmount().multiply(new BigDecimal(100)).longValue()));
        vnpParams.put("vnp_CurrCode", "VND");

        if (request.getBankCode() != null && !request.getBankCode().isEmpty()) {
            vnpParams.put("vnp_BankCode", request.getBankCode());
        }

        String orderInfo = request.getOrderInfo() != null ? request.getOrderInfo() : "Nap tien vao tai khoan " + accountNumber;

        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", orderInfo);
        vnpParams.put("vnp_OrderType", vnPayConfig.getOrderType());
        vnpParams.put("vnp_Locale", request.getLanguage() != null ? request.getLanguage() : "vn");
        vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());

        // Lấy IP của client
        String ipAddress = getClientIpAddress(httpRequest);
        vnpParams.put("vnp_IpAddr", ipAddress);

        // Thời gian tạo giao dịch
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParams.put("vnp_CreateDate", vnpCreateDate);

        // Thời gian hết hạn (15 phút)
        calendar.add(Calendar.MINUTE, 15);
        String vnpExpireDate = formatter.format(calendar.getTime());
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);

        // Tạo chuỗi hash và URL
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnpParams.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (Exception e) {
                    hashData.append(fieldValue);
                    query.append(fieldName).append('=').append(fieldValue);
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

        String paymentUrl = vnPayConfig.getUrl() + "?" + queryUrl;

        return VNPayDepositResponse.success(paymentUrl, txnRef, request.getAmount(), accountNumber, LocalDateTime.now());
    }

    /**
     * Xử lý callback từ VNPay sau khi thanh toán
     */
    @Transactional
    public VNPayCallbackResponse processCallback(Map<String, String> params) {
        String vnpSecureHash = params.get("vnp_SecureHash");

        // Loại bỏ các tham số hash để verify
        Map<String, String> fields = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null && !value.isEmpty()
                && !key.equals("vnp_SecureHash")
                && !key.equals("vnp_SecureHashType")) {
                fields.put(key, value);
            }
        }

        // Verify checksum
        String signValue = VNPayUtil.hashAllFields(fields, vnPayConfig.getHashSecret());

        VNPayCallbackResponse response = new VNPayCallbackResponse();

        if (!signValue.equals(vnpSecureHash)) {
            response.setSuccess(false);
            response.setMessage("Chữ ký không hợp lệ");
            return response;
        }

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");
        String bankCode = params.get("vnp_BankCode");
        String payDate = params.get("vnp_PayDate");
        String amountStr = params.get("vnp_Amount");
        String orderInfo = params.get("vnp_OrderInfo");

        response.setTxnRef(txnRef);
        response.setVnpTransactionNo(transactionNo);
        response.setBankCode(bankCode);

        BigDecimal amount = BigDecimal.ZERO;
        if (amountStr != null) {
            amount = new BigDecimal(amountStr).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            response.setAmount(amount);
        }

        if (payDate != null && payDate.length() == 14) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            response.setPayDate(LocalDateTime.parse(payDate, formatter));
        }

        // Kiểm tra kết quả thanh toán
        if ("00".equals(responseCode)) {
            // Lấy account number từ orderInfo hoặc tìm từ txnRef pattern
            String accountNumber = extractAccountNumber(orderInfo);

            if (accountNumber != null) {
                // Tìm tài khoản checking
                CheckingAccount checkingAccount = checkingAccountRepository
                        .findByAccountNumber(accountNumber)
                        .orElse(null);

                if (checkingAccount != null) {
                    // Cộng tiền vào tài khoản checking
                    BigDecimal newBalance = checkingAccount.getBalance().add(amount);
                    checkingAccount.setBalance(newBalance);
                    checkingAccountRepository.save(checkingAccount);

                    // Lưu transaction vào bảng transactions
                    Transaction transaction = new Transaction();
                    transaction.setSenderAccount(null); // Deposit từ bên ngoài
                    transaction.setReceiverAccount(checkingAccount.getAccount());
                    transaction.setAmount(amount);
                    transaction.setTransactionType(Transaction.TransactionType.DEPOSIT);
                    transaction.setDescription("Nạp tiền từ VNPay");
                    transaction.setCode(txnRef);
                    transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
                    transactionRepository.save(transaction);

                    response.setAccountNumber(accountNumber);
                    response.setSuccess(true);
                    response.setMessage("Nạp tiền thành công");
                    response.setStatus("SUCCESS");
                } else {
                    response.setSuccess(false);
                    response.setMessage("Không tìm thấy tài khoản");
                    response.setStatus("FAILED");
                }
            } else {
                response.setSuccess(false);
                response.setMessage("Không thể xác định tài khoản nhận tiền");
                response.setStatus("FAILED");
            }
        } else {
            response.setSuccess(false);
            response.setMessage(getResponseMessage(responseCode));
            response.setStatus("FAILED");
        }

        return response;
    }

    /**
     * Extract account number from order info
     */
    private String extractAccountNumber(String orderInfo) {
        if (orderInfo == null) return null;
        // Pattern: "Nap tien vao tai khoan XXXXXXXXXX"
        String prefix = "Nap tien vao tai khoan ";
        if (orderInfo.contains(prefix)) {
            return orderInfo.substring(orderInfo.indexOf(prefix) + prefix.length()).trim();
        }
        return null;
    }

    /**
     * Lấy IP của client
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress != null ? ipAddress : "127.0.0.1";
    }

    /**
     * Lấy message từ response code
     */
    private String getResponseMessage(String responseCode) {
        Map<String, String> messages = new HashMap<>();
        messages.put("00", "Giao dịch thành công");
        messages.put("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ.");
        messages.put("09", "Thẻ/Tài khoản chưa đăng ký Internet Banking.");
        messages.put("10", "Xác thực thông tin thẻ/tài khoản không đúng quá 3 lần.");
        messages.put("11", "Đã hết hạn chờ thanh toán.");
        messages.put("12", "Thẻ/Tài khoản bị khóa.");
        messages.put("13", "Nhập sai mật khẩu OTP.");
        messages.put("24", "Khách hàng hủy giao dịch.");
        messages.put("51", "Tài khoản không đủ số dư.");
        messages.put("65", "Vượt quá hạn mức giao dịch trong ngày.");
        messages.put("75", "Ngân hàng thanh toán đang bảo trì.");
        messages.put("79", "Nhập sai mật khẩu thanh toán quá số lần quy định.");
        messages.put("99", "Lỗi không xác định.");

        return messages.getOrDefault(responseCode, "Lỗi: " + responseCode);
    }
}
