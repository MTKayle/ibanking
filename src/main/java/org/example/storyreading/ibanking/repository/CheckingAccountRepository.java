package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.CheckingAccount;
import org.example.storyreading.ibanking.entity.Account;
import org.example.storyreading.ibanking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface CheckingAccountRepository extends JpaRepository<CheckingAccount, Long> {

    Optional<CheckingAccount> findByAccount(Account account);

    /**
     * Find CheckingAccount by account number with pessimistic write lock (SELECT FOR UPDATE)
     * This ensures no other transaction can modify the record until current transaction completes
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ca FROM CheckingAccount ca JOIN ca.account a WHERE a.accountNumber = :accountNumber")
    Optional<CheckingAccount> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);

    /**
     * Read-only find by account number (no lock) used for queries and permission checks
     */
    @Query("SELECT ca FROM CheckingAccount ca JOIN ca.account a WHERE a.accountNumber = :accountNumber")
    Optional<CheckingAccount> findByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT ca FROM CheckingAccount ca JOIN ca.account a WHERE a.user.userId = :userId AND a.accountType = 'checking'")
    Optional<CheckingAccount> findCheckingAccountsByUserId(@Param("userId") Long userId);

    @Query("SELECT ca FROM CheckingAccount ca WHERE ca.account.user = :user AND ca.account.accountType = 'checking'")
    List<CheckingAccount> findByUser(@Param("user") User user);

    // get first checking account by user id
    @Query("SELECT ca FROM CheckingAccount ca WHERE ca.account.user.userId = :userId AND ca.account.accountType = 'checking'")
    Optional<CheckingAccount> findFirstByUserId(@Param("userId") Long userId);

}
