package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByCccdNumber(String cccdNumber);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByCccdNumber(String cccdNumber);
}

