package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.TransactionHistoryDTO;
import org.example.storyreading.ibanking.entity.Transaction;
import org.example.storyreading.ibanking.entity.User;
import org.example.storyreading.ibanking.repository.TransactionRepository;
import org.example.storyreading.ibanking.repository.UserRepository;
import org.example.storyreading.ibanking.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionHistoryService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy tất cả giao dịch theo userId (dành cho officer)
     */
    @Transactional(readOnly = true)
    public List<TransactionHistoryDTO> getAllTransactionsByUserId(Long userId) {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));

        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);

        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy giao dịch thành công của user hiện tại (dành cho user)
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

        List<Transaction> transactions = transactionRepository.findSuccessfulTransactionsByUserId(currentUserId);

        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
        dto.setCreatedAt(transaction.getCreatedAt());

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
}

