// ...new file...
package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.MortgageAccount;
import org.example.storyreading.ibanking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MortgageAccountRepository extends JpaRepository<MortgageAccount, Long> {

    Optional<MortgageAccount> findByAccount(Account account);

}

