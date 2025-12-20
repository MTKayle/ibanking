package org.example.storyreading.ibanking.service.impl;

import org.example.storyreading.ibanking.dto.DepositRequest;
import org.example.storyreading.ibanking.dto.DepositResponse;
import org.example.storyreading.ibanking.dto.WithdrawRequest;
import org.example.storyreading.ibanking.dto.WithdrawResponse;
import org.example.storyreading.ibanking.dto.TransferRequest;
import org.example.storyreading.ibanking.dto.TransferResponse;
import org.example.storyreading.ibanking.dto.OtpResponse;
import org.example.storyreading.ibanking.entity.Account;
import org.example.storyreading.ibanking.entity.CheckingAccount;
import org.example.storyreading.ibanking.entity.Transaction;
import org.example.storyreading.ibanking.exception.ResourceNotFoundException;
import org.example.storyreading.ibanking.repository.AccountRepository;
import org.example.storyreading.ibanking.repository.CheckingAccountRepository;
import org.example.storyreading.ibanking.repository.TransactionRepository;
import org.example.storyreading.ibanking.security.CustomUserDetails;
import org.example.storyreading.ibanking.service.PaymentService;
import org.example.storyreading.ibanking.service.TransactionCreationService;
import org.example.storyreading.ibanking.service.TransactionFailureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionFailureService transactionFailureService;

    @Autowired
    private TransactionCreationService transactionCreationService;



    @Override
    @Transactional
    public DepositResponse depositToCheckingAccount(DepositRequest depositRequest) {
        String transactionCode = generateTransactionCode();

        try {
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

            // Bước 2.5: TẠO TRANSACTION PENDING trong transaction riêng biệt (REQUIRES_NEW)
            // Transaction này sẽ được commit ngay lập tức và KHÔNG bị rollback nếu có lỗi sau này
            transactionCreationService.createPendingTransaction(
                    null, // DEPOSIT không có sender account
                    account, // Account nhận tiền
                    depositRequest.getAmount(),
                    Transaction.TransactionType.DEPOSIT,
                    depositRequest.getDescription() != null
                            ? depositRequest.getDescription()
                            : "Nạp tiền vào tài khoản",
                    transactionCode
            );

            // Bước 3: Tính số dư mới
            BigDecimal currentBalance = checkingAccount.getBalance();
            BigDecimal newBalance = currentBalance.add(depositRequest.getAmount());

            // Bước 4: Cập nhật số dư
            checkingAccount.setBalance(newBalance);
            checkingAccountRepository.save(checkingAccount);

            // Bước 5: CẬP NHẬT TRANSACTION THÀNH SUCCESS trong transaction riêng biệt
            transactionFailureService.markSuccess(transactionCode);

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

        } catch (Exception e) {
            // Nếu có lỗi, gọi service riêng để lưu transaction FAILED
            // Service này sẽ tạo transaction mới để commit FAILED record
            // trong khi transaction này sẽ rollback số dư
            try {
                transactionFailureService.markFailed(transactionCode, e);
            } catch (Exception ignored) {
                // Ignore error khi lưu failed transaction
            }
            // Ném lại exception để rollback transaction database
            throw e;
        }
    }

    @Override
    @Transactional
    public TransferResponse transferMoney(TransferRequest transferRequest) {
        String transactionCode = generateTransferTransactionCode();

        try {
            // Bước 1: Validate request
            if (transferRequest.getSenderAccountNumber().equals(transferRequest.getReceiverAccountNumber())) {
                throw new IllegalArgumentException("Không thể chuyển tiền cho chính mình");
            }

            if (transferRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Số tiền chuyển phải lớn hơn 0");
            }

            // Bước 2: Tìm cả hai account (không lock) để xác định thứ tự lock
            CheckingAccount senderChecking = checkingAccountRepository
                    .findByAccountNumber(transferRequest.getSenderAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy tài khoản gửi: " + transferRequest.getSenderAccountNumber()));

            CheckingAccount receiverChecking = checkingAccountRepository
                    .findByAccountNumber(transferRequest.getReceiverAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy tài khoản nhận: " + transferRequest.getReceiverAccountNumber()));

            // Bước 3: Kiểm tra account type phải là checking
            Account senderAccount = senderChecking.getAccount();
            Account receiverAccount = receiverChecking.getAccount();

            if (senderAccount.getAccountType() != Account.AccountType.checking) {
                throw new IllegalArgumentException("Tài khoản gửi phải là tài khoản checking");
            }

            if (receiverAccount.getAccountType() != Account.AccountType.checking) {
                throw new IllegalArgumentException("Tài khoản nhận phải là tài khoản checking");
            }

            // Bước 3.5: KIỂM TRA QUYỀN SỞ HỮU - Tài khoản gửi có thuộc về user đang đăng nhập không
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                Long currentUserId = userDetails.getUserId();
                String currentUserRole = userDetails.getRole().name();

                // Chỉ CUSTOMER cần kiểm tra ownership, OFFICER và ADMIN có thể chuyển từ bất kỳ tài khoản nào
                if ("customer".equalsIgnoreCase(currentUserRole)) {
                    Long senderAccountOwnerId = senderAccount.getUser().getUserId();

                    if (!currentUserId.equals(senderAccountOwnerId)) {
                        throw new AccessDeniedException(
                                "Bạn không có quyền chuyển tiền từ tài khoản này. " +
                                        "Chỉ có thể chuyển tiền từ tài khoản của chính bạn.");
                    }
                }
            }

            // Bước 4: Kiểm tra trạng thái tài khoản
            if (senderAccount.getStatus() != Account.Status.active) {
                throw new IllegalStateException("Tài khoản gửi không hoạt động");
            }

            if (receiverAccount.getStatus() != Account.Status.active) {
                throw new IllegalStateException("Tài khoản nhận không hoạt động");
            }

            // Bước 4.5: TẠO TRANSACTION PENDING trong transaction riêng biệt (REQUIRES_NEW)
            // Transaction này sẽ được commit ngay lập tức và KHÔNG bị rollback nếu có lỗi sau này
            Transaction transaction = transactionCreationService.createPendingTransaction(
                    senderAccount,
                    receiverAccount,
                    transferRequest.getAmount(),
                    Transaction.TransactionType.TRANSFER,
                    transferRequest.getDescription() != null
                            ? transferRequest.getDescription()
                            : "Chuyển tiền",
                    transactionCode
            );

            // Bước 5: QUAN TRỌNG - Lock theo thứ tự accountId để tránh deadlock
            // Lock account có ID nhỏ hơn trước
            Long senderAccountId = senderAccount.getAccountId();
            Long receiverAccountId = receiverAccount.getAccountId();

            CheckingAccount firstLockAccount;
            CheckingAccount secondLockAccount;
            String firstAccountNumber;
            String secondAccountNumber;

            if (senderAccountId < receiverAccountId) {
                firstAccountNumber = transferRequest.getSenderAccountNumber();
                secondAccountNumber = transferRequest.getReceiverAccountNumber();
            } else {
                firstAccountNumber = transferRequest.getReceiverAccountNumber();
                secondAccountNumber = transferRequest.getSenderAccountNumber();
            }

            // Lock first account
            firstLockAccount = checkingAccountRepository
                    .findByAccountNumberForUpdate(firstAccountNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản: " + firstAccountNumber));

            // Lock second account
            secondLockAccount = checkingAccountRepository
                    .findByAccountNumberForUpdate(secondAccountNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản: " + secondAccountNumber));

            // Xác định đâu là sender và receiver sau khi lock
            CheckingAccount lockedSender;
            CheckingAccount lockedReceiver;

            if (firstLockAccount.getAccount().getAccountNumber().equals(transferRequest.getSenderAccountNumber())) {
                lockedSender = firstLockAccount;
                lockedReceiver = secondLockAccount;
            } else {
                lockedSender = secondLockAccount;
                lockedReceiver = firstLockAccount;
            }

            // Bước 6: Kiểm tra số dư (bao gồm cả overdraft limit)
            BigDecimal senderBalance = lockedSender.getBalance();
            BigDecimal overdraftLimit = lockedSender.getOverdraftLimit() != null
                    ? lockedSender.getOverdraftLimit()
                    : BigDecimal.ZERO;
            BigDecimal availableAmount = senderBalance.add(overdraftLimit);

            if (availableAmount.compareTo(transferRequest.getAmount()) < 0) {
                throw new IllegalArgumentException(
                        String.format("Số dư không đủ. Số dư khả dụng: %.2f, Số tiền chuyển: %.2f",
                                availableAmount, transferRequest.getAmount()));
            }

            // Bước 7: Thực hiện chuyển tiền
            BigDecimal senderNewBalance = senderBalance.subtract(transferRequest.getAmount());
            BigDecimal receiverNewBalance = lockedReceiver.getBalance().add(transferRequest.getAmount());

            lockedSender.setBalance(senderNewBalance);
            lockedReceiver.setBalance(receiverNewBalance);

            checkingAccountRepository.save(lockedSender);
            checkingAccountRepository.save(lockedReceiver);

            // Bước 8: CẬP NHẬT TRANSACTION THÀNH SUCCESS trong transaction riêng biệt
            transactionFailureService.markSuccess(transactionCode);

            // Bước 9: Tạo response
            TransferResponse response = new TransferResponse();
            response.setTransactionId(transaction.getTransactionId());
            response.setTransactionCode(transactionCode);
            response.setSenderAccountNumber(transferRequest.getSenderAccountNumber());
            response.setReceiverAccountNumber(transferRequest.getReceiverAccountNumber());
            response.setAmount(transferRequest.getAmount());
            response.setDescription(transferRequest.getDescription());
            response.setSenderNewBalance(senderNewBalance);
            response.setReceiverNewBalance(receiverNewBalance);
            response.setReceiverUserFullName(
                    lockedReceiver.getAccount().getUser().getFullName()
            );
            response.setTransactionTime(transaction.getCreatedAt());
            response.setStatus("SUCCESS");

            return response;

        } catch (Exception e) {
            // Nếu có lỗi, gọi service riêng để lưu transaction FAILED
            // Service này sẽ tạo transaction mới để commit FAILED record
            // trong khi transaction này sẽ rollback số dư
            try {
                transactionFailureService.markFailed(transactionCode, e);
            } catch (Exception ignored) {
                // Ignore error khi lưu failed transaction
            }
            // Ném lại exception để rollback transaction database
            throw e;
        }
    }

    /**
     * Generate unique transaction code for deposits
     * Format: DEP-{timestamp}-{random}
     */
    private String generateTransactionCode() {
        return String.format("DEP-%d-%s",
                System.currentTimeMillis(),
                UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    /**
     * Generate unique transaction code for transfers
     * Format: TRF-{timestamp}-{random}
     */
    private String generateTransferTransactionCode() {
        return String.format("TRF-%d-%s",
                System.currentTimeMillis(),
                UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    @Override
    @Transactional
    public OtpResponse initiateTransferWithOtp(TransferRequest transferRequest) {
        String transactionCode = generateTransferTransactionCode();

        try {
            // Bước 1: Validate request
            if (transferRequest.getSenderAccountNumber().equals(transferRequest.getReceiverAccountNumber())) {
                throw new IllegalArgumentException("Không thể chuyển tiền cho chính mình");
            }

            if (transferRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Số tiền chuyển phải lớn hơn 0");
            }

            // Bước 2: Tìm cả hai account (không lock)
            CheckingAccount senderChecking = checkingAccountRepository
                    .findByAccountNumber(transferRequest.getSenderAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy tài khoản gửi: " + transferRequest.getSenderAccountNumber()));

            CheckingAccount receiverChecking = checkingAccountRepository
                    .findByAccountNumber(transferRequest.getReceiverAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy tài khoản nhận: " + transferRequest.getReceiverAccountNumber()));

            // Bước 3: Kiểm tra account type phải là checking
            Account senderAccount = senderChecking.getAccount();
            Account receiverAccount = receiverChecking.getAccount();

            if (senderAccount.getAccountType() != Account.AccountType.checking) {
                throw new IllegalArgumentException("Tài khoản gửi phải là tài khoản checking");
            }

            if (receiverAccount.getAccountType() != Account.AccountType.checking) {
                throw new IllegalArgumentException("Tài khoản nhận phải là tài khoản checking");
            }

            // Bước 4: KIỂM TRA QUYỀN SỞ HỮU
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                Long currentUserId = userDetails.getUserId();
                String currentUserRole = userDetails.getRole().name();

                if ("customer".equalsIgnoreCase(currentUserRole)) {
                    Long senderAccountOwnerId = senderAccount.getUser().getUserId();

                    if (!currentUserId.equals(senderAccountOwnerId)) {
                        throw new AccessDeniedException(
                                "Bạn không có quyền chuyển tiền từ tài khoản này.");
                    }
                }
            }

            // Bước 5: Kiểm tra trạng thái tài khoản
            if (senderAccount.getStatus() != Account.Status.active) {
                throw new IllegalStateException("Tài khoản gửi không hoạt động");
            }

            if (receiverAccount.getStatus() != Account.Status.active) {
                throw new IllegalStateException("Tài khoản nhận không hoạt động");
            }

            // Bước 6: Kiểm tra số dư (không lock, chỉ check)
            BigDecimal senderBalance = senderChecking.getBalance();
            BigDecimal overdraftLimit = senderChecking.getOverdraftLimit() != null
                    ? senderChecking.getOverdraftLimit()
                    : BigDecimal.ZERO;
            BigDecimal availableAmount = senderBalance.add(overdraftLimit);

            if (availableAmount.compareTo(transferRequest.getAmount()) < 0) {
                throw new IllegalArgumentException(
                        String.format("Số dư không đủ. Số dư khả dụng: %.2f, Số tiền chuyển: %.2f",
                                availableAmount, transferRequest.getAmount()));
            }

            // Bước 7: TẠO TRANSACTION PENDING
            Transaction transaction = transactionCreationService.createPendingTransaction(
                    senderAccount,
                    receiverAccount,
                    transferRequest.getAmount(),
                    Transaction.TransactionType.TRANSFER,
                    transferRequest.getDescription() != null
                            ? transferRequest.getDescription()
                            : "Chuyển tiền",
                    transactionCode
            );

            return new OtpResponse(
                    transactionCode,
                    "Mã giao dich đã được tạo. Vui lòng sử dụng mã OTP để xác nhận giao dịch."
            );

        } catch (Exception e) {
            // Nếu có lỗi, mark transaction as failed
            try {
                transactionFailureService.markFailed(transactionCode, e);
            } catch (Exception ignored) {
                // Ignore
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public TransferResponse confirmTransferWithOtp(String transactionCode) {
        try {
            // Bước 2: Tìm transaction
            Transaction transaction = transactionRepository.findByCode(transactionCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionCode));

            // Bước 3: Kiểm tra transaction phải ở trạng thái PENDING
            if (transaction.getStatus() != Transaction.TransactionStatus.PENDING) {
                throw new IllegalStateException("Transaction is not in PENDING status");
            }

            // Bước 4: Lấy thông tin accounts
            Account senderAccount = transaction.getSenderAccount();
            Account receiverAccount = transaction.getReceiverAccount();

            if (senderAccount == null || receiverAccount == null) {
                throw new IllegalStateException("Invalid transaction: missing sender or receiver account");
            }

            // Bước 5: Lock accounts theo thứ tự để tránh deadlock
            Long senderAccountId = senderAccount.getAccountId();
            Long receiverAccountId = receiverAccount.getAccountId();

            String firstAccountNumber;
            String secondAccountNumber;

            if (senderAccountId < receiverAccountId) {
                firstAccountNumber = senderAccount.getAccountNumber();
                secondAccountNumber = receiverAccount.getAccountNumber();
            } else {
                firstAccountNumber = receiverAccount.getAccountNumber();
                secondAccountNumber = senderAccount.getAccountNumber();
            }

            // Lock accounts
            CheckingAccount firstLockAccount = checkingAccountRepository
                    .findByAccountNumberForUpdate(firstAccountNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + firstAccountNumber));

            CheckingAccount secondLockAccount = checkingAccountRepository
                    .findByAccountNumberForUpdate(secondAccountNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + secondAccountNumber));

            // Xác định đâu là sender và receiver
            CheckingAccount lockedSender;
            CheckingAccount lockedReceiver;

            if (firstLockAccount.getAccount().getAccountNumber().equals(senderAccount.getAccountNumber())) {
                lockedSender = firstLockAccount;
                lockedReceiver = secondLockAccount;
            } else {
                lockedSender = secondLockAccount;
                lockedReceiver = firstLockAccount;
            }

            // Bước 6: Kiểm tra số dư lại (có thể đã thay đổi)
            BigDecimal senderBalance = lockedSender.getBalance();
            BigDecimal overdraftLimit = lockedSender.getOverdraftLimit() != null
                    ? lockedSender.getOverdraftLimit()
                    : BigDecimal.ZERO;
            BigDecimal availableAmount = senderBalance.add(overdraftLimit);

            if (availableAmount.compareTo(transaction.getAmount()) < 0) {
                throw new IllegalArgumentException("Số dư không đủ để thực hiện giao dịch");
            }

            // Bước 7: Thực hiện chuyển tiền
            BigDecimal senderNewBalance = senderBalance.subtract(transaction.getAmount());
            BigDecimal receiverNewBalance = lockedReceiver.getBalance().add(transaction.getAmount());

            lockedSender.setBalance(senderNewBalance);
            lockedReceiver.setBalance(receiverNewBalance);

            checkingAccountRepository.save(lockedSender);
            checkingAccountRepository.save(lockedReceiver);

            // Bước 8: Cập nhật transaction thành SUCCESS
            transactionFailureService.markSuccess(transactionCode);

            // Bước 9: Tạo response
            TransferResponse response = new TransferResponse();
            response.setTransactionId(transaction.getTransactionId());
            response.setTransactionCode(transactionCode);
            response.setSenderAccountNumber(senderAccount.getAccountNumber());
            response.setReceiverAccountNumber(receiverAccount.getAccountNumber());
            response.setAmount(transaction.getAmount());
            response.setDescription(transaction.getDescription());
            response.setSenderNewBalance(senderNewBalance);
            response.setReceiverNewBalance(receiverNewBalance);
            response.setReceiverUserFullName(receiverAccount.getUser().getFullName());
            response.setTransactionTime(transaction.getCreatedAt());
            response.setStatus("SUCCESS");

            return response;

        } catch (Exception e) {
            // Mark transaction as failed
            try {
                transactionFailureService.markFailed(transactionCode, e);
            } catch (Exception ignored) {
                // Ignore
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public WithdrawResponse withdrawFromCheckingAccount(WithdrawRequest withdrawRequest) {
        String transactionCode = generateTransactionCode();

        try {
            // Bước 1: Tìm checking account đầu tiên của user với pessimistic lock
            CheckingAccount checkingAccount = checkingAccountRepository
                    .findFirstByUserId(withdrawRequest.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy tài khoản checking cho user ID: " + withdrawRequest.getUserId()));

            // Lock tài khoản để tránh race condition
            CheckingAccount lockedAccount = checkingAccountRepository
                    .findByAccountNumberForUpdate(checkingAccount.getAccount().getAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không thể lock tài khoản checking"));

            // Bước 2: Kiểm tra trạng thái tài khoản
            Account account = lockedAccount.getAccount();
            if (account.getStatus() != Account.Status.active) {
                throw new IllegalStateException("Tài khoản không ở trạng thái hoạt động. Không thể rút tiền.");
            }

            // Bước 3: Kiểm tra số dư
            BigDecimal currentBalance = lockedAccount.getBalance();
            if (currentBalance.compareTo(withdrawRequest.getAmount()) < 0) {
                throw new IllegalArgumentException("Số dư không đủ để rút tiền. Số dư hiện tại: " + currentBalance);
            }

            // Bước 4: TẠO TRANSACTION PENDING
            transactionCreationService.createPendingTransaction(
                    account, // Account rút tiền
                    null, // WITHDRAW không có receiver account
                    withdrawRequest.getAmount(),
                    Transaction.TransactionType.WITHDRAW,
                    withdrawRequest.getDescription() != null
                            ? withdrawRequest.getDescription()
                            : "Rút tiền từ tài khoản",
                    transactionCode
            );

            // Bước 5: Tính số dư mới
            BigDecimal newBalance = currentBalance.subtract(withdrawRequest.getAmount());

            // Bước 6: Cập nhật số dư
            lockedAccount.setBalance(newBalance);
            checkingAccountRepository.save(lockedAccount);

            // Bước 7: CẬP NHẬT TRANSACTION THÀNH SUCCESS
            transactionFailureService.markSuccess(transactionCode);

            // Bước 8: Tạo response
            Instant timestamp = Instant.now();
            String message = String.format("Rút tiền thành công %.2f từ tài khoản %s. Mã giao dịch: %s",
                    withdrawRequest.getAmount(), account.getAccountNumber(), transactionCode);

            WithdrawResponse response = new WithdrawResponse();
            response.setAccountNumber(account.getAccountNumber());
            response.setWithdrawAmount(withdrawRequest.getAmount());
            response.setNewBalance(newBalance);
            response.setDescription(withdrawRequest.getDescription());
            response.setTimestamp(timestamp);
            response.setMessage(message);

            return response;

        } catch (Exception e) {
            // Nếu có lỗi, gọi service riêng để lưu transaction FAILED
            try {
                transactionFailureService.markFailed(transactionCode, e);
            } catch (Exception ignored) {
                // Ignore error khi lưu failed transaction
            }
            // Ném lại exception để rollback transaction database
            throw e;
        }
    }

}
