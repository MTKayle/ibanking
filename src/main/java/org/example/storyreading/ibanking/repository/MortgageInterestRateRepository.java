package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.MortgageInterestRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MortgageInterestRateRepository extends JpaRepository<MortgageInterestRate, Long> {

    @Query("SELECT m FROM MortgageInterestRate m WHERE m.minMonths <= :termMonths AND (m.maxMonths IS NULL OR m.maxMonths >= :termMonths)")
    Optional<MortgageInterestRate> findByTermMonths(@Param("termMonths") Integer termMonths);
}

