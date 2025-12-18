package org.example.storyreading.ibanking.repository;

import jakarta.persistence.LockModeType;
import org.example.storyreading.ibanking.entity.UtilityBill;
import org.example.storyreading.ibanking.entity.UtilityBillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilityBillRepository extends JpaRepository<UtilityBill, Long> {

    /**
     * Tìm hóa đơn theo mã hóa đơn
     */
    Optional<UtilityBill> findByBillCode(String billCode);

    /**
     * Tìm hóa đơn theo mã hóa đơn với PESSIMISTIC LOCK (SELECT FOR UPDATE)
     * Để tránh 2 user cùng thanh toán 1 hóa đơn
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM UtilityBill b WHERE b.billCode = :billCode")
    Optional<UtilityBill> findByBillCodeWithLock(@Param("billCode") String billCode);

    /**
     * Lấy tất cả hóa đơn của user đã thanh toán
     */
    @Query("SELECT b FROM UtilityBill b WHERE b.paidByUser.userId = :userId ORDER BY b.paymentTime DESC")
    List<UtilityBill> findByPaidByUserId(@Param("userId") Long userId);

    /**
     * Tìm hóa đơn theo status
     */
    List<UtilityBill> findByStatus(UtilityBillStatus status);

    /**
     * Tìm hóa đơn quá hạn (dueDate < today và status = UNPAID)
     */
    @Query("SELECT b FROM UtilityBill b WHERE b.dueDate < :today AND b.status = 'UNPAID'")
    List<UtilityBill> findOverdueBills(@Param("today") LocalDate today);
}
