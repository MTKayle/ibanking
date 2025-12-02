// ...new file...
package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.SavingAccount;
import org.example.storyreading.ibanking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavingAccountRepository extends JpaRepository<SavingAccount, String> {

    Optional<SavingAccount> findByAccount(Account account);

}

