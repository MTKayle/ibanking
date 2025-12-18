package org.example.storyreading.ibanking.controller;

import org.example.storyreading.ibanking.dto.movie.CinemaScreeningResponseDTO;
import org.example.storyreading.ibanking.dto.movie.MovieDetailResponseDTO;
import org.example.storyreading.ibanking.dto.movie.MovieListResponseDTO;
import org.example.storyreading.ibanking.dto.movie.ScreeningDetailResponseDTO;
import org.example.storyreading.ibanking.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    /**
     * API lấy danh sách tất cả phim đang chiếu
     * GET /api/movies
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMovies() {
        try {
            List<MovieListResponseDTO> movies = movieService.getAllMovies();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách phim thành công");
            response.put("data", movies);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách phim: " + e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * API lấy chi tiết phim theo ID
     * GET /api/movies/{movieId}
     */
    @GetMapping("/{movieId}")
    public ResponseEntity<Map<String, Object>> getMovieDetail(@PathVariable Long movieId) {
        try {
            MovieDetailResponseDTO movie = movieService.getMovieDetail(movieId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy chi tiết phim thành công");
            response.put("data", movie);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy chi tiết phim: " + e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * API lấy danh sách rạp và suất chiếu theo phim và ngày
     * GET /api/movies/{movieId}/screenings?date=YYYY-MM-DD
     */
    @GetMapping("/{movieId}/screenings")
    public ResponseEntity<Map<String, Object>> getCinemaScreenings(
            @PathVariable Long movieId,
            @RequestParam(name = "date") String dateStr) {
        try {
            // Parse date từ string
            LocalDate screeningDate = LocalDate.parse(dateStr);

            // Validate: Không cho phép lấy suất chiếu của ngày quá khứ
            if (screeningDate.isBefore(LocalDate.now())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Không thể lấy suất chiếu của ngày đã qua");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            List<CinemaScreeningResponseDTO> cinemaScreenings =
                    movieService.getCinemaScreeningsByMovieAndDate(movieId, screeningDate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách rạp và suất chiếu thành công");
            response.put("data", cinemaScreenings);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy suất chiếu: " + e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * API lấy chi tiết suất chiếu và danh sách ghế
     * GET /api/screenings/{screeningId}
     */
    @GetMapping("/screenings/{screeningId}")
    public ResponseEntity<Map<String, Object>> getScreeningDetail(@PathVariable Long screeningId) {
        try {
            ScreeningDetailResponseDTO screeningDetail = movieService.getScreeningDetail(screeningId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy chi tiết suất chiếu thành công");
            response.put("data", screeningDetail);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy chi tiết suất chiếu: " + e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
