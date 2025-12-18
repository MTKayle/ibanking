package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    /**
     * Lấy tất cả ghế của một phòng chiếu, sắp xếp theo hàng và số ghế
     */
    @Query("SELECT s FROM Seat s WHERE s.cinemaHall.hallId = :hallId ORDER BY s.rowLabel, s.seatNumber")
    List<Seat> findByHallIdOrderByRowAndNumber(@Param("hallId") Long hallId);
}

