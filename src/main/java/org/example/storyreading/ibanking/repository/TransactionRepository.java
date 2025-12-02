package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.Transaction;
import org.example.storyreading.ibanking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByCode(String code);

    // Query transactions where account is sender
    List<Transaction> findBySenderAccount(Account account);

    // Query transactions where account is receiver
    List<Transaction> findByReceiverAccount(Account account);

    // Query all transactions related to an account (either sender or receiver)
    @Query("SELECT t FROM Transaction t WHERE t.senderAccount.accountId = :accountId OR t.receiverAccount.accountId = :accountId ORDER BY t.createdAt DESC")
    List<Transaction> findAllByAccountId(@Param("accountId") Long accountId);

    // Query by account number
    @Query("SELECT t FROM Transaction t WHERE t.senderAccount.accountNumber = :accountNumber OR t.receiverAccount.accountNumber = :accountNumber ORDER BY t.createdAt DESC")
    List<Transaction> findAllByAccountNumber(@Param("accountNumber") String accountNumber);
}
