// ...new file...
package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.SavingAccount;
import org.example.storyreading.ibanking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SavingAccountRepository extends JpaRepository<SavingAccount, Long> {

    Optional<SavingAccount> findByAccount(Account account);

    Optional<SavingAccount> findBySavingBookNumber(String savingBookNumber);

    @Query("SELECT s FROM SavingAccount s WHERE s.account.user.userId = :userId")
    List<SavingAccount> findByUserId(@Param("userId") Long userId);

    @Query("SELECT s FROM SavingAccount s WHERE s.account.accountNumber = :accountNumber")
    Optional<SavingAccount> findByAccountNumber(@Param("accountNumber") String accountNumber);

    boolean existsBySavingBookNumber(String savingBookNumber);
}
