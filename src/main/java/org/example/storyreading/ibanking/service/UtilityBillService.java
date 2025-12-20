package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.utility.UtilityBillPaymentRequestDTO;
import org.example.storyreading.ibanking.dto.utility.UtilityBillPaymentResponseDTO;
import org.example.storyreading.ibanking.dto.utility.UtilityBillResponseDTO;
import org.example.storyreading.ibanking.entity.*;
import org.example.storyreading.ibanking.repository.CheckingAccountRepository;
import org.example.storyreading.ibanking.repository.TransactionRepository;
import org.example.storyreading.ibanking.repository.UserRepository;
import org.example.storyreading.ibanking.repository.UtilityBillRepository;
import org.example.storyreading.ibanking.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UtilityBillService {

    @Autowired
    private UtilityBillRepository utilityBillRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Tìm kiếm hóa đơn theo mã hóa đơn
     */
    @Transactional(readOnly = true)
    public UtilityBillResponseDTO findByBillCodeAndBillType(String billCode, String billType) {
        // Convert String billType sang enum
        UtilityBillType billTypeEnum;
        try {
            billTypeEnum = UtilityBillType.valueOf(billType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Loại hóa đơn không hợp lệ: " + billType +
                ". Các loại hợp lệ: ELECTRICITY, WATER, INTERNET, PHONE");
        }

        UtilityBill bill = utilityBillRepository.findByBillCodeAndBillType(billCode, billTypeEnum)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + billCode));

        // KHÔNG update trong readOnly transaction - chỉ convert sang DTO
        UtilityBillResponseDTO dto = convertToDTO(bill);

        // Check if overdue và set vào DTO (không update entity)
        if (bill.getStatus() == UtilityBillStatus.UNPAID &&
            bill.getDueDate().isBefore(LocalDate.now())) {
            dto.setOverdue(true);
            dto.setStatus("OVERDUE");
            dto.setStatusDisplay("Quá hạn");
        }

        return dto;
    }

    /**
     * Thanh toán hóa đơn
     * Sử dụng PESSIMISTIC LOCK để tránh 2 user cùng thanh toán 1 hóa đơn
     */
    @Transactional
    public UtilityBillPaymentResponseDTO payBill(UtilityBillPaymentRequestDTO request) {
        // Lấy thông tin user hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("Không thể xác thực người dùng. Vui lòng đăng nhập lại");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = userDetails.getUserId();

        if (currentUserId == null) {
            throw new RuntimeException("Không tìm thấy thông tin userId. Vui lòng đăng nhập lại");
        }

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + currentUserId));

        // Lấy checking account của user với EAGER load Account
        CheckingAccount checkingAccount = checkingAccountRepository.findFirstByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản thanh toán cho user ID: " + currentUserId));

        // Đảm bảo Account entity được load
        Account account = checkingAccount.getAccount();
        if (account == null) {
            throw new RuntimeException("Không thể lấy thông tin Account từ CheckingAccount");
        }

        // Tìm hóa đơn với PESSIMISTIC LOCK (SELECT FOR UPDATE) để tránh thanh toán trùng
        UtilityBill bill = utilityBillRepository.findByBillCodeWithLock(request.getBillCode())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + request.getBillCode()));

        // Kiểm tra trạng thái hóa đơn
        if (bill.getStatus() == UtilityBillStatus.PAID) {
            throw new RuntimeException("Hóa đơn này đã được thanh toán");
        }

        if (bill.getStatus() == UtilityBillStatus.CANCELLED) {
            throw new RuntimeException("Hóa đơn này đã bị hủy");
        }

        // Kiểm tra số dư
        BigDecimal currentBalance = checkingAccount.getBalance();
        if (currentBalance == null) {
            throw new RuntimeException("Không thể lấy số dư tài khoản");
        }

        BigDecimal billAmount = bill.getTotalAmount();
        if (billAmount == null) {
            throw new RuntimeException("Số tiền hóa đơn không hợp lệ");
        }

        if (currentBalance.compareTo(billAmount) < 0) {
            throw new RuntimeException("Số dư không đủ để thanh toán hóa đơn. Số dư hiện tại: "
                + currentBalance + " VND, Số tiền cần thanh toán: " + billAmount + " VND");
        }

        // Trừ tiền từ tài khoản
        BigDecimal newBalance = currentBalance.subtract(billAmount);
        checkingAccount.setBalance(newBalance);
        checkingAccountRepository.save(checkingAccount);

        // Tạo transaction code
        String transactionCode = "UTILITY_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Tạo transaction (WITHDRAW type - chỉ có sender account)
        Transaction transaction = new Transaction(
            account,  // senderAccount
            null,     // receiverAccount (null cho WITHDRAW)
            billAmount,
            Transaction.TransactionType.WITHDRAW,
            "Thanh toán " + bill.getBillType().getDisplayName() +
                " - Mã HĐ: " + bill.getBillCode() + " - " + bill.getProviderName(),
            transactionCode
        );
        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Cập nhật trạng thái hóa đơn
        bill.setStatus(UtilityBillStatus.PAID);
        bill.setPaymentTime(LocalDateTime.now());
        bill.setPaidByUser(user);
        bill.setTransactionId(savedTransaction.getTransactionId());
        utilityBillRepository.save(bill);

        // Tạo response
        UtilityBillPaymentResponseDTO response = new UtilityBillPaymentResponseDTO();
        response.setBillCode(bill.getBillCode());
        response.setAmount(billAmount);
        response.setStatus("SUCCESS");
        response.setPaymentTime(bill.getPaymentTime());
        response.setTransactionId(savedTransaction.getTransactionId().toString());
        response.setBalanceAfter(newBalance);
        response.setMessage("Thanh toán hóa đơn thành công");

        return response;
    }

    /**
     * Lấy danh sách hóa đơn đã thanh toán của user
     */
    @Transactional(readOnly = true)
    public List<UtilityBillResponseDTO> getMyPaidBills() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("Không thể xác thực người dùng. Vui lòng đăng nhập lại");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = userDetails.getUserId();

        if (currentUserId == null) {
            throw new RuntimeException("Không tìm thấy thông tin userId. Vui lòng đăng nhập lại");
        }

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + currentUserId));

        List<UtilityBill> bills = utilityBillRepository.findByPaidByUserId(user.getUserId());
        return bills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả hóa đơn chưa thanh toán
     */
    @Transactional(readOnly = true)
    public List<UtilityBillResponseDTO> getAllUnpaidBills() {
        List<UtilityBill> bills = utilityBillRepository.findByStatus(UtilityBillStatus.UNPAID);
        return bills.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Entity sang DTO
     */
    private UtilityBillResponseDTO convertToDTO(UtilityBill bill) {
        UtilityBillResponseDTO dto = new UtilityBillResponseDTO();

        dto.setBillId(bill.getBillId());
        dto.setBillCode(bill.getBillCode());
        dto.setBillType(bill.getBillType().name());
        dto.setBillTypeDisplay(bill.getBillType().getDisplayName());
        dto.setCustomerName(bill.getCustomerName());
        dto.setCustomerAddress(bill.getCustomerAddress());
        dto.setCustomerPhone(bill.getCustomerPhone());
        dto.setPeriod(bill.getPeriod());
        dto.setUsageAmount(bill.getUsageAmount());
        dto.setOldIndex(bill.getOldIndex());
        dto.setNewIndex(bill.getNewIndex());
        dto.setUnitPrice(bill.getUnitPrice());
        dto.setAmount(bill.getAmount());
        dto.setVat(bill.getVat());
        dto.setTotalAmount(bill.getTotalAmount());
        dto.setIssueDate(bill.getIssueDate());
        dto.setDueDate(bill.getDueDate());
        dto.setStatus(bill.getStatus().name());
        dto.setStatusDisplay(bill.getStatus().getDisplayName());
        dto.setProviderName(bill.getProviderName());
        dto.setProviderCode(bill.getProviderCode());
        dto.setNotes(bill.getNotes());

        // Check if overdue
        dto.setOverdue(bill.getStatus() == UtilityBillStatus.UNPAID &&
                       bill.getDueDate().isBefore(LocalDate.now()));

        return dto;
    }
}
