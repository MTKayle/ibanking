package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.ExternalTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExternalTransferRepository extends JpaRepository<ExternalTransfer, Long> {

    // Tìm giao dịch theo transaction code
    Optional<ExternalTransfer> findByTransactionCode(String transactionCode);

    // Tìm tất cả giao dịch của một account
    List<ExternalTransfer> findBySenderAccount_AccountId(Long accountId);

    // Tìm tất cả giao dịch của một user
    List<ExternalTransfer> findBySenderAccount_User_UserId(Long userId);

    // Tìm giao dịch theo status
    List<ExternalTransfer> findByStatus(ExternalTransfer.TransferStatus status);

    // Kiểm tra transaction code đã tồn tại chưa
    boolean existsByTransactionCode(String transactionCode);
}

