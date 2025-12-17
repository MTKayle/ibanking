package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {

    Optional<Bank> findByBankBin(String bankBin);

    Optional<Bank> findByBankCode(String bankCode);
}

