package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.MovieScreening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieScreeningRepository extends JpaRepository<MovieScreening, Long> {

    /**
     * Lấy tất cả suất chiếu của một phim theo ngày
     * Chỉ lấy các suất chiếu chưa bắt đầu (giờ chiếu > giờ hiện tại)
     */
    @Query("SELECT ms FROM MovieScreening ms " +
           "JOIN FETCH ms.cinemaHall ch " +
           "JOIN FETCH ch.cinema c " +
           "WHERE ms.movie.movieId = :movieId " +
           "AND ms.screeningDate = :screeningDate " +
           "AND ms.startTime > :currentTime " +
           "ORDER BY c.cinemaId, ms.startTime")
    List<MovieScreening> findByMovieAndDateWithUpcomingScreenings(
            @Param("movieId") Long movieId,
            @Param("screeningDate") LocalDate screeningDate,
            @Param("currentTime") LocalTime currentTime
    );

    /**
     * Lấy chi tiết suất chiếu kèm theo movie và cinema hall
     */
    @Query("SELECT ms FROM MovieScreening ms " +
           "JOIN FETCH ms.movie m " +
           "JOIN FETCH ms.cinemaHall ch " +
           "JOIN FETCH ch.cinema c " +
           "WHERE ms.screeningId = :screeningId")
    Optional<MovieScreening> findByIdWithDetails(@Param("screeningId") Long screeningId);
}
