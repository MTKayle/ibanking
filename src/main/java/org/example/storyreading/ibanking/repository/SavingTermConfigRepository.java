package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.SavingTermConfig;
import org.example.storyreading.ibanking.entity.SavingTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavingTermConfigRepository extends JpaRepository<SavingTermConfig, Long> {

    Optional<SavingTermConfig> findByTermType(SavingTerm termType);

    boolean existsByTermType(SavingTerm termType);
}

