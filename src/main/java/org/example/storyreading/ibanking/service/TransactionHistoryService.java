package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.TransactionHistoryDTO;
import org.example.storyreading.ibanking.entity.Transaction;
import org.example.storyreading.ibanking.entity.User;
import org.example.storyreading.ibanking.entity.CheckingAccount;
import org.example.storyreading.ibanking.entity.ExternalTransfer;
import org.example.storyreading.ibanking.repository.TransactionRepository;
import org.example.storyreading.ibanking.repository.UserRepository;
import org.example.storyreading.ibanking.repository.CheckingAccountRepository;
import org.example.storyreading.ibanking.repository.ExternalTransferRepository;
import org.example.storyreading.ibanking.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionHistoryService {

    // Múi giờ Việt Nam
    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Autowired
    private ExternalTransferRepository externalTransferRepository;

    /**
     * Lấy tất cả giao dịch theo userId (dành cho officer)
     * Bao gồm cả giao dịch nội bộ và giao dịch ngoài ngân hàng
     */
    @Transactional(readOnly = true)
    public List<TransactionHistoryDTO> getAllTransactionsByUserId(Long userId) {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));

        List<TransactionHistoryDTO> allTransactions = new ArrayList<>();

        // 1. Lấy giao dịch nội bộ (transactions table)
        List<Transaction> internalTransactions = transactionRepository.findAllByUserId(userId);
        List<TransactionHistoryDTO> internalDTOs = internalTransactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        allTransactions.addAll(internalDTOs);

        // 2. Lấy giao dịch ngoài ngân hàng (external_transfers table)
        // Tìm checking account đầu tiên của user
        CheckingAccount checkingAccount = checkingAccountRepository.findFirstByUserId(userId)
                .orElse(null);

        if (checkingAccount != null) {
            // Tìm tất cả giao dịch ngoài từ account này
            List<ExternalTransfer> externalTransfers = externalTransferRepository
                    .findBySenderAccount_AccountId(checkingAccount.getAccount().getAccountId());

            List<TransactionHistoryDTO> externalDTOs = externalTransfers.stream()
                    .map(this::convertExternalTransferToDTO)
                    .collect(Collectors.toList());
            allTransactions.addAll(externalDTOs);
        }

        // 3. Sắp xếp tất cả giao dịch theo thời gian giảm dần (mới nhất trước)
        allTransactions.sort(Comparator.comparing(TransactionHistoryDTO::getCreatedAt).reversed());

        return allTransactions;
    }

    /**
     * Lấy chi tiết giao dịch cùng ngân hàng (internal transaction) theo ID
     */
    @Transactional(readOnly = true)
    public TransactionHistoryDTO getInternalTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + transactionId));

        return convertToDTO(transaction);
    }

    /**
     * Lấy chi tiết giao dịch ngoài ngân hàng (external transaction) theo ID
     */
    @Transactional(readOnly = true)
    public TransactionHistoryDTO getExternalTransactionById(Long externalTransferId) {
        ExternalTransfer transfer = externalTransferRepository.findById(externalTransferId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch ngoài ngân hàng với ID: " + externalTransferId));

        return convertExternalTransferToDTO(transfer);
    }

    /**
     * Lấy lịch sử giao dịch tiền vào (DEPOSIT + nhận chuyển khoản)
     * Chỉ lấy các giao dịch thành công
     */
    @Transactional(readOnly = true)
    public List<TransactionHistoryDTO> getMyIncomingTransactions() {
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

        // 1. Tìm checking account đầu tiên của user
        CheckingAccount checkingAccount = checkingAccountRepository.findFirstByUserId(currentUserId)
                .orElse(null);

        if (checkingAccount == null) {
            return new ArrayList<>(); // Trả về list rỗng nếu không có checking account
        }

        Long accountId = checkingAccount.getAccount().getAccountId();

        // 2. Lấy tất cả giao dịch nội bộ thành công
        List<Transaction> allTransactions = transactionRepository.findSuccessfulTransactionsByUserId(currentUserId);

        // 3. Lọc chỉ lấy giao dịch tiền vào - receiverAccount có accountId trùng với checking account
        List<TransactionHistoryDTO> incomingTransactions = allTransactions.stream()
                .filter(transaction ->
                    transaction.getReceiverAccount() != null &&
                    transaction.getReceiverAccount().getAccountId().equals(accountId)
                )
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 4. Sắp xếp theo thời gian giảm dần
        incomingTransactions.sort(Comparator.comparing(TransactionHistoryDTO::getCreatedAt).reversed());

        return incomingTransactions;
    }

    /**
     * Lấy lịch sử giao dịch tiền ra (WITHDRAW + chuyển khoản đi + chuyển ngoài ngân hàng)
     * Chỉ lấy các giao dịch thành công
     */
    @Transactional(readOnly = true)
    public List<TransactionHistoryDTO> getMyOutgoingTransactions() {
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

        List<TransactionHistoryDTO> outgoingTransactions = new ArrayList<>();

        // 1. Tìm checking account đầu tiên của user
        CheckingAccount checkingAccount = checkingAccountRepository.findFirstByUserId(currentUserId)
                .orElse(null);

        if (checkingAccount == null) {
            return outgoingTransactions; // Trả về list rỗng nếu không có checking account
        }

        Long accountId = checkingAccount.getAccount().getAccountId();

        // 2. Lấy giao dịch nội bộ tiền ra - senderAccount có accountId trùng với checking account
        List<Transaction> allTransactions = transactionRepository.findSuccessfulTransactionsByUserId(currentUserId);

        List<TransactionHistoryDTO> internalOutgoing = allTransactions.stream()
                .filter(transaction ->
                    transaction.getSenderAccount() != null &&
                    transaction.getSenderAccount().getAccountId().equals(accountId)
                )
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        outgoingTransactions.addAll(internalOutgoing);

        // 3. Lấy giao dịch ngoài ngân hàng (chuyển tiền ra ngoài)
        List<ExternalTransfer> externalTransfers = externalTransferRepository
                .findBySenderAccount_AccountId(accountId)
                .stream()
                .filter(transfer -> transfer.getStatus() == ExternalTransfer.TransferStatus.SUCCESS)
                .collect(Collectors.toList());

        List<TransactionHistoryDTO> externalDTOs = externalTransfers.stream()
                .map(this::convertExternalTransferToDTO)
                .collect(Collectors.toList());
        outgoingTransactions.addAll(externalDTOs);

        // 4. Sắp xếp tất cả giao dịch tiền ra theo thời gian giảm dần
        outgoingTransactions.sort(Comparator.comparing(TransactionHistoryDTO::getCreatedAt).reversed());

        return outgoingTransactions;
    }

    /**
     * Lấy giao dịch thành công của user hiện tại (dành cho user)
     * Bao gồm cả giao dịch nội bộ và giao dịch ngoài ngân hàng
     */
    @Transactional(readOnly = true)
    public List<TransactionHistoryDTO> getMySuccessfulTransactions() {
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

        List<TransactionHistoryDTO> allTransactions = new ArrayList<>();

        // 1. Lấy giao dịch nội bộ (transactions table)
        List<Transaction> internalTransactions = transactionRepository.findSuccessfulTransactionsByUserId(currentUserId);

        List<TransactionHistoryDTO> internalDTOs = internalTransactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        allTransactions.addAll(internalDTOs);

        // 2. Lấy giao dịch ngoài ngân hàng (external_transfers table)
        // Tìm checking account đầu tiên của user
        CheckingAccount checkingAccount = checkingAccountRepository.findFirstByUserId(currentUserId)
                .orElse(null);

        if (checkingAccount != null) {
            String accountNumber = checkingAccount.getAccount().getAccountNumber();

            // Tìm tất cả giao dịch ngoài thành công từ account này
            List<ExternalTransfer> externalTransfers = externalTransferRepository
                    .findBySenderAccount_AccountId(checkingAccount.getAccount().getAccountId())
                    .stream()
                    .filter(transfer -> transfer.getStatus() == ExternalTransfer.TransferStatus.SUCCESS)
                    .collect(Collectors.toList());

            List<TransactionHistoryDTO> externalDTOs = externalTransfers.stream()
                    .map(this::convertExternalTransferToDTO)
                    .collect(Collectors.toList());
            allTransactions.addAll(externalDTOs);
        }

        // 3. Sắp xếp tất cả giao dịch theo thời gian giảm dần (mới nhất trước)
        allTransactions.sort(Comparator.comparing(TransactionHistoryDTO::getCreatedAt).reversed());

        return allTransactions;
    }

    /**
     * Convert Transaction entity sang DTO
     */
    private TransactionHistoryDTO convertToDTO(Transaction transaction) {
        TransactionHistoryDTO dto = new TransactionHistoryDTO();

        dto.setTransactionId(transaction.getTransactionId());
        dto.setCode(transaction.getCode());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionType(transaction.getTransactionType().name());
        dto.setDescription(transaction.getDescription());
        dto.setStatus(transaction.getStatus().name());

        // Chuyển đổi Instant sang LocalDateTime với múi giờ Việt Nam
        if (transaction.getCreatedAt() != null) {
            dto.setCreatedAt(LocalDateTime.ofInstant(transaction.getCreatedAt(), VIETNAM_ZONE));
        }

        // Set sender account info (có thể null cho DEPOSIT)
        if (transaction.getSenderAccount() != null) {
            dto.setSenderAccountNumber(transaction.getSenderAccount().getAccountNumber());
            if (transaction.getSenderAccount().getUser() != null) {
                dto.setSenderAccountName(transaction.getSenderAccount().getUser().getFullName());
            }
        }

        // Set receiver account info (có thể null cho WITHDRAW)
        if (transaction.getReceiverAccount() != null) {
            dto.setReceiverAccountNumber(transaction.getReceiverAccount().getAccountNumber());
            if (transaction.getReceiverAccount().getUser() != null) {
                dto.setReceiverAccountName(transaction.getReceiverAccount().getUser().getFullName());
            }
        }

        return dto;
    }

    /**
     * Convert ExternalTransfer entity sang DTO
     */
    private TransactionHistoryDTO convertExternalTransferToDTO(ExternalTransfer transfer) {
        TransactionHistoryDTO dto = new TransactionHistoryDTO();

        dto.setTransactionId(transfer.getExternalTransferId());
        dto.setCode(transfer.getTransactionCode());
        dto.setAmount(transfer.getAmount());
        dto.setTransactionType("EXTERNAL_TRANSFER"); // Loại giao dịch mới
        dto.setDescription(transfer.getDescription());
        dto.setStatus(transfer.getStatus().name());

        // Chuyển đổi Instant sang LocalDateTime với múi giờ Việt Nam
        if (transfer.getCreatedAt() != null) {
            dto.setCreatedAt(LocalDateTime.ofInstant(transfer.getCreatedAt(), VIETNAM_ZONE));
        }
        if (transfer.getCompletedAt() != null) {
            dto.setCompletedAt(LocalDateTime.ofInstant(transfer.getCompletedAt(), VIETNAM_ZONE));
        }

        // Sender info
        if (transfer.getSenderAccount() != null) {
            dto.setSenderAccountNumber(transfer.getSenderAccount().getAccountNumber());
            if (transfer.getSenderAccount().getUser() != null) {
                dto.setSenderAccountName(transfer.getSenderAccount().getUser().getFullName());
            }
        }

        // Receiver info (ngoài hệ thống)
        dto.setReceiverAccountNumber(transfer.getReceiverAccountNumber());
        dto.setReceiverAccountName(transfer.getReceiverName());
        dto.setReceiverBankName(transfer.getReceiverBank().getBankName());
        dto.setReceiverBankBin(transfer.getReceiverBank().getBankBin());

        return dto;
    }

    /**
     * Lấy chi tiết giao dịch theo mã code
     * Tìm trong cả bảng transactions và external_transfers
     */
    @Transactional(readOnly = true)
    public TransactionHistoryDTO getTransactionByCode(String code) {
        // 1. Tìm trong bảng transactions (internal)
        Transaction transaction = transactionRepository.findByCode(code).orElse(null);

        if (transaction != null) {
            return convertToDTO(transaction);
        }

        // 2. Nếu không tìm thấy, tìm trong bảng external_transfers
        ExternalTransfer externalTransfer = externalTransferRepository.findByTransactionCode(code).orElse(null);

        if (externalTransfer != null) {
            return convertExternalTransferToDTO(externalTransfer);
        }

        // 3. Không tìm thấy trong cả 2 bảng
        throw new RuntimeException("Không tìm thấy giao dịch với mã: " + code);
    }
}
