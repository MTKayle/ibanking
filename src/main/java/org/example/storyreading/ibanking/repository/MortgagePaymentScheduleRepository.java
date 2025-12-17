package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.MortgageAccount;
import org.example.storyreading.ibanking.entity.MortgagePaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MortgagePaymentScheduleRepository extends JpaRepository<MortgagePaymentSchedule, Long> {

    List<MortgagePaymentSchedule> findByMortgageAccountOrderByPeriodNumberAsc(MortgageAccount mortgageAccount);

    @Query("SELECT m FROM MortgagePaymentSchedule m WHERE m.mortgageAccount = :mortgageAccount AND (m.status = 'PENDING' OR m.status = 'DUE' OR m.status = 'OVERDUE') ORDER BY m.periodNumber ASC")
    List<MortgagePaymentSchedule> findUnpaidSchedules(@Param("mortgageAccount") MortgageAccount mortgageAccount);

    @Query("SELECT m FROM MortgagePaymentSchedule m WHERE m.mortgageAccount = :mortgageAccount AND m.status = :status ORDER BY m.periodNumber ASC")
    List<MortgagePaymentSchedule> findByMortgageAccountAndStatus(@Param("mortgageAccount") MortgageAccount mortgageAccount,
                                                                   @Param("status") MortgagePaymentSchedule.PaymentStatus status);

    @Query("SELECT m FROM MortgagePaymentSchedule m WHERE m.dueDate <= :date AND (m.status = 'PENDING' OR m.status = 'DUE') ORDER BY m.dueDate ASC")
    List<MortgagePaymentSchedule> findOverdueSchedules(@Param("date") LocalDate date);

    Optional<MortgagePaymentSchedule> findByMortgageAccountAndPeriodNumber(MortgageAccount mortgageAccount, Integer periodNumber);
}
