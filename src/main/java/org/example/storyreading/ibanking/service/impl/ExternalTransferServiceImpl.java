package org.example.storyreading.ibanking.service.impl;

import org.example.storyreading.ibanking.dto.ExternalTransferRequest;
import org.example.storyreading.ibanking.dto.ExternalTransferResponse;
import org.example.storyreading.ibanking.dto.OtpResponse;
import org.example.storyreading.ibanking.entity.*;
import org.example.storyreading.ibanking.exception.ResourceNotFoundException;
import org.example.storyreading.ibanking.repository.*;
import org.example.storyreading.ibanking.service.ExternalTransferService;
import org.example.storyreading.ibanking.service.TransactionFailureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

@Service
public class ExternalTransferServiceImpl implements ExternalTransferService {

    @Autowired
    private ExternalTransferRepository externalTransferRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private ExternalBankAccountRepository externalBankAccountRepository;


    private final Random random = new Random();

    @Override
    @Transactional
    public OtpResponse initiateExternalTransfer(ExternalTransferRequest request) {
        // 1. Tìm sender account
        Account senderAccount = accountRepository.findByAccountNumber(request.getSenderAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tài khoản gửi: " + request.getSenderAccountNumber()));

        // Kiểm tra account type phải là checking
        if (senderAccount.getAccountType() != Account.AccountType.checking) {
            throw new IllegalArgumentException("Chỉ có thể chuyển tiền từ tài khoản checking");
        }

        // Kiểm tra account status
        if (senderAccount.getStatus() != Account.Status.active) {
            throw new IllegalArgumentException("Tài khoản gửi không ở trạng thái hoạt động");
        }

//        // 2. Tìm receiver bank theo BIN
//        Bank receiverBank = bankRepository.findByBankBin(request.getReceiverBankBin())
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Không tìm thấy ngân hàng với BIN: " + request.getReceiverBankBin()));

        //2. ti receiver bank theo bankBin and accoutNumber
        ExternalBankAccount account = externalBankAccountRepository
                .findByBankBinAndAccountNumber(request.getReceiverBankBin(), request.getReceiverAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tài khoản với số tài khoản " + request.getReceiverAccountNumber())
                );

        // 3. Kiểm tra số dư tài khoản sender
        CheckingAccount senderChecking = checkingAccountRepository.findByAccount(senderAccount)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy checking account"));

        if (senderChecking.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("Số dư không đủ để thực hiện giao dịch");
        }

        // 4. Validate amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0");
        }

        // 5. Tạo transaction code và OTP
        String transactionCode = generateUniqueTransactionCode();
        String otpCode = generateOtp();
        Instant otpExpiry = Instant.now().plus(1, ChronoUnit.MINUTES);

        // 6. Tạo ExternalTransfer entity
        ExternalTransfer transfer = new ExternalTransfer();
        transfer.setSenderAccount(senderAccount);
        transfer.setReceiverBank(account.getBank());
        transfer.setReceiverAccountNumber(request.getReceiverAccountNumber());
        transfer.setReceiverName(request.getReceiverName());
        transfer.setAmount(request.getAmount());
        transfer.setDescription(request.getDescription());
        transfer.setTransactionCode(transactionCode);
        transfer.setOtpCode(otpCode);
        transfer.setOtpExpiry(otpExpiry);
        transfer.setStatus(ExternalTransfer.TransferStatus.PENDING);

        externalTransferRepository.save(transfer);

        // 7. Trả về OTP response
        OtpResponse response = new OtpResponse();
        response.setTransactionCode(transactionCode);
        response.setMessage("Mã giao dịch đã được tạo thành công. Vui lòng xác nhận");

        return response;
    }

    @Override
    @Transactional
    public ExternalTransferResponse confirmExternalTransfer(String transactionCode) {
        // 1. Tìm giao dịch theo transaction code
        ExternalTransfer transfer = externalTransferRepository.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy giao dịch với mã: " + transactionCode));

        // 2. Kiểm tra trạng thái giao dịch
        if (transfer.getStatus() != ExternalTransfer.TransferStatus.PENDING) {
            throw new IllegalArgumentException("Giao dịch không ở trạng thái chờ xác nhận");
        }



        try {
            // 5. Lock và trừ tiền từ tài khoản sender
            CheckingAccount senderChecking = checkingAccountRepository
                    .findByAccountNumberForUpdate(transfer.getSenderAccount().getAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản gửi"));

            // Kiểm tra lại số dư (có thể đã thay đổi trong khi chờ)
            if (senderChecking.getBalance().compareTo(transfer.getAmount()) < 0) {
                transfer.setStatus(ExternalTransfer.TransferStatus.FAILED);
                transfer.setCompletedAt(Instant.now());
                externalTransferRepository.save(transfer);


                throw new IllegalArgumentException("Số dư không đủ để thực hiện giao dịch");
            }

            // Trừ tiền từ sender
            BigDecimal newBalance = senderChecking.getBalance().subtract(transfer.getAmount());
            senderChecking.setBalance(newBalance);
            checkingAccountRepository.save(senderChecking);

            // 6. Cập nhật trạng thái giao dịch thành SUCCESS
            transfer.setStatus(ExternalTransfer.TransferStatus.SUCCESS);
            transfer.setCompletedAt(Instant.now());
            externalTransferRepository.save(transfer);

            // 7. Tạo response
            ExternalTransferResponse response = new ExternalTransferResponse();
            response.setTransactionCode(transfer.getTransactionCode());
            response.setSenderAccountNumber(transfer.getSenderAccount().getAccountNumber());
            response.setReceiverBankName(transfer.getReceiverBank().getBankName());
            response.setReceiverBankBin(transfer.getReceiverBank().getBankBin());
            response.setReceiverAccountNumber(transfer.getReceiverAccountNumber());
            response.setReceiverName(transfer.getReceiverName());
            response.setAmount(transfer.getAmount());
            response.setDescription(transfer.getDescription());
            response.setStatus(transfer.getStatus().name());
            response.setSenderNewBalance(newBalance);
            response.setMessage("Chuyển tiền ngoài ngân hàng thành công");
            response.setCreatedAt(transfer.getCreatedAt().toString());
            response.setCompletedAt(transfer.getCompletedAt().toString());

            return response;

        } catch (Exception e) {
            // Nếu có lỗi, cập nhật trạng thái thành FAILED
            transfer.setStatus(ExternalTransfer.TransferStatus.FAILED);
            transfer.setCompletedAt(Instant.now());
            externalTransferRepository.save(transfer);
            throw new RuntimeException("Chuyển tiền thất bại: " + e.getMessage());
        }
    }

    /**
     * Generate unique transaction code
     */
    private String generateUniqueTransactionCode() {
        String code;
        do {
            code = "EXT" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (externalTransferRepository.existsByTransactionCode(code));
        return code;
    }

    /**
     * Generate 6-digit OTP
     */
    private String generateOtp() {
        return String.format("%06d", random.nextInt(1000000));
    }
}

