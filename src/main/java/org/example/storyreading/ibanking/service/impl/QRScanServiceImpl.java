package org.example.storyreading.ibanking.service.impl;

import org.example.storyreading.ibanking.dto.QRScanRequest;
import org.example.storyreading.ibanking.dto.QRScanResponse;
import org.example.storyreading.ibanking.entity.Account;
import org.example.storyreading.ibanking.entity.Bank;
import org.example.storyreading.ibanking.entity.User;
import org.example.storyreading.ibanking.exception.BadRequestException;
import org.example.storyreading.ibanking.exception.ResourceNotFoundException;
import org.example.storyreading.ibanking.repository.AccountRepository;
import org.example.storyreading.ibanking.repository.BankRepository;
import org.example.storyreading.ibanking.service.QRScanService;
import org.example.storyreading.ibanking.utils.VietQRParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class QRScanServiceImpl implements QRScanService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BankRepository bankRepository;

    @Override
    @Transactional(readOnly = true)
    public QRScanResponse scanQRCode(QRScanRequest request) {
        // Bước 1: Parse QR content
        Map<String, String> qrData = VietQRParser.parseVietQR(request.getQrContent());

        if (qrData.isEmpty()) {
            throw new BadRequestException("Nội dung QR không hợp lệ");
        }

        // Bước 2: Lấy account number từ QR
        String accountNumber = qrData.get("accountNumber");
        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new BadRequestException("Không tìm thấy số tài khoản trong QR code");
        }

        // Bước 3: Tìm account trong database
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản: " + accountNumber));

        // Bước 4: Kiểm tra account type phải là checking
        if (account.getAccountType() != Account.AccountType.checking) {
            throw new BadRequestException("Tài khoản không phải là tài khoản checking. Chỉ hỗ trợ chuyển tiền vào tài khoản checking.");
        }

        // Bước 5: Kiểm tra account status
        if (account.getStatus() != Account.Status.active) {
            throw new BadRequestException("Tài khoản không hoạt động");
        }

        // Bước 6: Lấy user info
        User user = account.getUser();
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin người dùng");
        }

        // Bước 7: Lấy bank info từ QR hoặc từ user
        String bankBinFromQR = qrData.get("bankBin");
        Bank bank = null;

        // Ưu tiên lấy bank từ user nếu user đã có bank
        if (user.getBank() != null) {
            bank = user.getBank();

            // Validate bank BIN trong QR phải khớp với bank của user (nếu có trong QR)
            if (bankBinFromQR != null && !bankBinFromQR.equals(bank.getBankBin())) {
                throw new BadRequestException("Mã ngân hàng trong QR không khớp với ngân hàng của tài khoản");
            }
//        } else if (bankBinFromQR != null) {
//            // Nếu user chưa có bank, tìm bank theo BIN trong QR
//            bank = bankRepository.findByBankBin(bankBinFromQR)
//                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin ngân hàng với BIN: " + bankBinFromQR));
        } else {
            throw new BadRequestException("Không tìm thấy thông tin ngân hàng");
        }

        // Bước 8: Parse amount và description từ QR (nếu có)
        BigDecimal amount = null;
        String amountStr = qrData.get("amount");
        if (amountStr != null && !amountStr.isEmpty()) {
            amount = VietQRParser.parseAmount(amountStr);
        }

        String description = qrData.get("description");

        // Bước 9: Tạo response
        QRScanResponse response = new QRScanResponse();
        response.setAccountNumber(accountNumber);
        response.setAccountHolderName(user.getFullName());
        response.setBankBin(bank.getBankBin());
        response.setBankCode(bank.getBankCode());
        response.setBankName(bank.getBankName());
        response.setAmount(amount);
        response.setDescription(description);
        response.setUserId(user.getUserId());
        response.setAccountType(account.getAccountType().name());

        return response;
    }
}

