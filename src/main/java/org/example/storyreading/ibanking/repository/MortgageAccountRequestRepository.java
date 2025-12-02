// ...new file...
package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.MortgageAccountRequest;
import org.example.storyreading.ibanking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MortgageAccountRequestRepository extends JpaRepository<MortgageAccountRequest, Long> {

    List<MortgageAccountRequest> findByUser(User user);

    List<MortgageAccountRequest> findByUserUserId(Long userId);

    Optional<MortgageAccountRequest> findByRequestId(Long requestId);

    List<MortgageAccountRequest> findByStatus(MortgageAccountRequest.RequestStatus status);

}

