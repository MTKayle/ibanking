package org.example.storyreading.ibanking.repository;

import org.example.storyreading.ibanking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Lấy tất cả phim đang chiếu
    List<Movie> findByIsShowingTrue();

    // Lấy phim kèm theo screenings để tránh N+1 query
    @Query("SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.screenings WHERE m.movieId = :movieId")
    Optional<Movie> findByIdWithScreenings(@Param("movieId") Long movieId);

    // Lấy tất cả phim kèm theo screenings
    @Query("SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.screenings WHERE m.isShowing = true")
    List<Movie> findAllShowingWithScreenings();
}

