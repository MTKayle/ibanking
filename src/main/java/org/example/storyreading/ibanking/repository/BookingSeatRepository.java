package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    /**
     * Lấy tất cả ghế đã được đặt cho một suất chiếu cụ thể
     */
    @Query("SELECT bs FROM BookingSeat bs WHERE bs.screening.screeningId = :screeningId AND bs.status IN ('BOOKED', 'RESERVED')")
    List<BookingSeat> findBookedSeatsByScreeningId(@Param("screeningId") Long screeningId);
}

