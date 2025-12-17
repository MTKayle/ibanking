// ...new file...
package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.MortgageAccount;
import org.example.storyreading.ibanking.entity.Account;
import org.example.storyreading.ibanking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MortgageAccountRepository extends JpaRepository<MortgageAccount, Long> {

    Optional<MortgageAccount> findByAccount(Account account);

    @Query("SELECT m FROM MortgageAccount m WHERE m.account.accountNumber = :accountNumber")
    Optional<MortgageAccount> findByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT m FROM MortgageAccount m WHERE m.account.user = :user")
    List<MortgageAccount> findByUser(@Param("user") User user);

    @Query("SELECT m FROM MortgageAccount m WHERE m.status = :status")
    List<MortgageAccount> findByStatus(@Param("status") MortgageAccount.MortgageStatus status);

    //find bay status and phone number
    @Query("SELECT m FROM MortgageAccount m WHERE m.status = :status AND m.account.user.phone = :phoneNumber")
    List<MortgageAccount> findByStatusAndPhoneNumber(@Param("status") MortgageAccount.MortgageStatus status,
                                                  @Param("phoneNumber") String phoneNumber);

    @Query("SELECT m FROM MortgageAccount m WHERE m.account.user.userId = :userId")
    List<MortgageAccount> findByUserId(@Param("userId") Long userId);
}
