package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.TransactionOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionOtpRepository extends JpaRepository<TransactionOtp, Long> {

    Optional<TransactionOtp> findByTransaction_TransactionId(Long transactionId);

    @Query("SELECT t FROM TransactionOtp t WHERE t.transaction.code = :transactionCode")
    Optional<TransactionOtp> findByTransactionCode(@Param("transactionCode") String transactionCode);

    boolean existsByTransaction_TransactionId(Long transactionId);
}

