package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Lấy tất cả booking của user, sắp xếp theo thời gian mới nhất
     */
    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.screening s " +
           "JOIN FETCH s.movie m " +
           "JOIN FETCH s.cinemaHall ch " +
           "JOIN FETCH ch.cinema c " +
           "WHERE b.user.userId = :userId " +
           "ORDER BY b.bookingTime DESC")
    List<Booking> findByUserIdWithDetails(@Param("userId") Long userId);
}
