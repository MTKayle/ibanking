package org.example.storyreading.ibanking.service.impl;

import org.example.storyreading.ibanking.dto.DepositRequest;
import org.example.storyreading.ibanking.dto.DepositResponse;
import org.example.storyreading.ibanking.entity.Account;
import org.example.storyreading.ibanking.entity.CheckingAccount;
import org.example.storyreading.ibanking.entity.Transaction;
import org.example.storyreading.ibanking.exception.ResourceNotFoundException;
import org.example.storyreading.ibanking.repository.AccountRepository;
import org.example.storyreading.ibanking.repository.CheckingAccountRepository;
import org.example.storyreading.ibanking.repository.TransactionRepository;
import org.example.storyreading.ibanking.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    @Transactional
    public DepositResponse depositToCheckingAccount(DepositRequest depositRequest) {
        // Bước 1: Tìm checking account với pessimistic lock (SELECT FOR UPDATE)
        CheckingAccount checkingAccount = checkingAccountRepository
                .findByAccountNumberForUpdate(depositRequest.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tài khoản checking với số: " + depositRequest.getAccountNumber()));

        // Bước 2: Kiểm tra trạng thái tài khoản
        Account account = checkingAccount.getAccount();
        if (account.getStatus() != Account.Status.active) {
            throw new IllegalStateException("Tài khoản không ở trạng thái hoạt động. Không thể nạp tiền.");
        }

        // Bước 3: Tính số dư mới
        BigDecimal currentBalance = checkingAccount.getBalance();
        BigDecimal newBalance = currentBalance.add(depositRequest.getAmount());

        // Bước 4: Cập nhật số dư
        checkingAccount.setBalance(newBalance);
        checkingAccountRepository.save(checkingAccount);

        // Bước 5: Ghi giao dịch vào bảng transactions
        // Đối với DEPOSIT: không có sender (nạp từ bên ngoài), chỉ có receiver
        String transactionCode = generateTransactionCode();
        Transaction transaction = new Transaction();
        transaction.setSenderAccount(null); // DEPOSIT không có sender account
        transaction.setReceiverAccount(account); // Account nhận tiền
        transaction.setAmount(depositRequest.getAmount());
        transaction.setTransactionType(Transaction.TransactionType.DEPOSIT);
        transaction.setDescription(depositRequest.getDescription() != null
                ? depositRequest.getDescription()
                : "Nạp tiền vào tài khoản");
        transaction.setCode(transactionCode);
        transactionRepository.save(transaction);

        // Bước 6: Tạo response
        Instant timestamp = Instant.now();
        String message = String.format("Nạp tiền thành công %.2f vào tài khoản %s. Mã giao dịch: %s",
                depositRequest.getAmount(), depositRequest.getAccountNumber(), transactionCode);

        DepositResponse response = new DepositResponse();
        response.setAccountNumber(depositRequest.getAccountNumber());
        response.setDepositAmount(depositRequest.getAmount());
        response.setNewBalance(newBalance);
        response.setDescription(depositRequest.getDescription());
        response.setTimestamp(timestamp);
        response.setMessage(message);

        return response;
    }

    /**
     * Generate unique transaction code
     * Format: DEP-{timestamp}-{random}
     */
    private String generateTransactionCode() {
        return String.format("DEP-%d-%s",
                System.currentTimeMillis(),
                UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
}
