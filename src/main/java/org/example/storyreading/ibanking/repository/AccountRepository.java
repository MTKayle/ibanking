// ...new file...
package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.Account;
import org.example.storyreading.ibanking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByUser(User user);

    List<Account> findByUserUserId(Long userId);
}

