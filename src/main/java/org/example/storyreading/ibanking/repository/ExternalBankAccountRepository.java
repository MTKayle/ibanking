package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.ExternalBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExternalBankAccountRepository extends JpaRepository<ExternalBankAccount, Long> {

    // Tìm tài khoản theo bank ID và account number
    Optional<ExternalBankAccount> findByBank_BankIdAndAccountNumber(Long bankId, String accountNumber);

    // Tìm tài khoản theo bank BIN và account number (join với bảng banks)
    @Query("SELECT e FROM ExternalBankAccount e WHERE e.bank.bankBin = :bankBin AND e.accountNumber = :accountNumber")
    Optional<ExternalBankAccount> findByBankBinAndAccountNumber(@Param("bankBin") String bankBin, @Param("accountNumber") String accountNumber);
}

