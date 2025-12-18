package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.movie.CinemaScreeningResponseDTO;
import org.example.storyreading.ibanking.dto.movie.MovieDetailResponseDTO;
import org.example.storyreading.ibanking.dto.movie.MovieListResponseDTO;
import org.example.storyreading.ibanking.dto.movie.ScreeningResponseDTO;
import org.example.storyreading.ibanking.dto.movie.ScreeningDetailResponseDTO;
import org.example.storyreading.ibanking.dto.movie.SeatDetailDTO;
import org.example.storyreading.ibanking.dto.movie.SeatTypePriceDTO;
import org.example.storyreading.ibanking.entity.*;
import org.example.storyreading.ibanking.repository.BookingSeatRepository;
import org.example.storyreading.ibanking.repository.MovieRepository;
import org.example.storyreading.ibanking.repository.MovieScreeningRepository;
import org.example.storyreading.ibanking.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieScreeningRepository movieScreeningRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    /**
     * Lấy danh sách tất cả phim đang chiếu
     */
    @Transactional(readOnly = true)
    public List<MovieListResponseDTO> getAllMovies() {
        List<Movie> movies = movieRepository.findAllShowingWithScreenings();

        return movies.stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết phim theo ID
     */
    @Transactional(readOnly = true)
    public MovieDetailResponseDTO getMovieDetail(Long movieId) {
        Movie movie = movieRepository.findByIdWithScreenings(movieId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim với ID: " + movieId));

        return convertToDetailDTO(movie);
    }

    /**
     * Convert Movie entity sang MovieListResponseDTO
     */
    private MovieListResponseDTO convertToListDTO(Movie movie) {
        MovieListResponseDTO dto = new MovieListResponseDTO();
        dto.setMovieId(movie.getMovieId());
        dto.setTitle(movie.getTitle());
        dto.setGenre(movie.getGenre().name());
        dto.setGenreDisplay(movie.getGenre().getDisplayName());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setDurationMinutes(movie.getDurationMinutes());
        dto.setAgeRating(movie.getAgeRating());
        dto.setPosterUrl(movie.getPosterUrl());

        // Lấy tất cả các loại screening type của phim
        Set<ScreeningType> screeningTypes = getScreeningTypes(movie);
        dto.setScreeningTypes(screeningTypes);

        return dto;
    }

    /**
     * Convert Movie entity sang MovieDetailResponseDTO
     */
    private MovieDetailResponseDTO convertToDetailDTO(Movie movie) {
        MovieDetailResponseDTO dto = new MovieDetailResponseDTO();
        dto.setMovieId(movie.getMovieId());
        dto.setTitle(movie.getTitle());
        dto.setGenre(movie.getGenre().name());
        dto.setGenreDisplay(movie.getGenre().getDisplayName());
        dto.setDurationMinutes(movie.getDurationMinutes());

        // Lấy tất cả các loại screening type của phim
        Set<ScreeningType> screeningTypes = getScreeningTypes(movie);
        dto.setScreeningTypes(screeningTypes);

        dto.setTrailerUrl(movie.getTrailerUrl());
        dto.setPosterUrl(movie.getPosterUrl());
        dto.setDescription(movie.getDescription());
        dto.setDirector(movie.getDirector());
        dto.setCast(movie.getCast());
        dto.setAgeRating(movie.getAgeRating());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setLanguage(movie.getLanguage() != null ? movie.getLanguage().name() : null);
        dto.setLanguageDisplay(movie.getLanguage() != null ? movie.getLanguage().getDisplayName() : null);
        dto.setCountry(movie.getCountry());

        return dto;
    }

    /**
     * Lấy danh sách các screening types từ các suất chiếu của phim
     */
    private Set<ScreeningType> getScreeningTypes(Movie movie) {
        Set<ScreeningType> screeningTypes = new HashSet<>();

        if (movie.getScreenings() != null && !movie.getScreenings().isEmpty()) {
            for (MovieScreening screening : movie.getScreenings()) {
                screeningTypes.add(screening.getScreeningType());
            }
        }

        return screeningTypes;
    }

    /**
     * Lấy danh sách rạp và suất chiếu theo phim và ngày
     * Chỉ lấy các suất chiếu chưa bắt đầu
     */
    @Transactional(readOnly = true)
    public List<CinemaScreeningResponseDTO> getCinemaScreeningsByMovieAndDate(Long movieId, LocalDate screeningDate) {
        // Kiểm tra phim có tồn tại không
        movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim với ID: " + movieId));

        // Lấy giờ hiện tại
        LocalTime currentTime = LocalTime.now();

        // Nếu ngày được chọn là ngày hôm nay, chỉ lấy suất chiếu sau giờ hiện tại
        // Nếu là ngày mai trở đi, lấy tất cả suất chiếu
        LocalTime timeFilter = screeningDate.equals(LocalDate.now()) ? currentTime : LocalTime.MIN;

        // Lấy tất cả suất chiếu của phim theo ngày
        List<MovieScreening> screenings = movieScreeningRepository
                .findByMovieAndDateWithUpcomingScreenings(movieId, screeningDate, timeFilter);

        if (screenings.isEmpty()) {
            return new ArrayList<>();
        }

        // Nhóm suất chiếu theo rạp
        Map<Cinema, List<MovieScreening>> screeningsByCinema = screenings.stream()
                .collect(Collectors.groupingBy(
                        screening -> screening.getCinemaHall().getCinema(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        // Convert sang DTO
        return screeningsByCinema.entrySet().stream()
                .map(entry -> convertToCinemaScreeningDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Convert Cinema và danh sách MovieScreening sang CinemaScreeningResponseDTO
     */
    private CinemaScreeningResponseDTO convertToCinemaScreeningDTO(Cinema cinema, List<MovieScreening> screenings) {
        CinemaScreeningResponseDTO dto = new CinemaScreeningResponseDTO();
        dto.setCinemaId(cinema.getCinemaId());
        dto.setCinemaName(cinema.getName());
        dto.setCinemaAddress(cinema.getAddress());
        dto.setCinemaPhone(cinema.getPhone());
        dto.setCity(cinema.getCity());
        dto.setLatitude(cinema.getLatitude());
        dto.setLongitude(cinema.getLongitude());

        // Convert screenings sang DTO
        List<ScreeningResponseDTO> screeningDTOs = screenings.stream()
                .map(this::convertToScreeningDTO)
                .collect(Collectors.toList());
        dto.setScreenings(screeningDTOs);

        return dto;
    }

    /**
     * Convert MovieScreening sang ScreeningResponseDTO
     */
    private ScreeningResponseDTO convertToScreeningDTO(MovieScreening screening) {
        ScreeningResponseDTO dto = new ScreeningResponseDTO();
        dto.setScreeningId(screening.getScreeningId());
        dto.setHallId(screening.getCinemaHall().getHallId());
        dto.setHallName(screening.getCinemaHall().getHallName());
        dto.setStartTime(screening.getStartTime());
        dto.setEndTime(screening.getEndTime());
        dto.setScreeningType(screening.getScreeningType());
        dto.setScreeningTypeDisplay(screening.getScreeningType().getDisplayName());
        dto.setAvailableSeats(screening.getAvailableSeats());
        dto.setTotalSeats(screening.getCinemaHall().getTotalSeats());
        dto.setPriceMultiplier(screening.getPriceMultiplier());

        return dto;
    }

    /**
     * Lấy chi tiết suất chiếu bao gồm tất cả ghế
     */
    @Transactional(readOnly = true)
    public ScreeningDetailResponseDTO getScreeningDetail(Long screeningId) {
        // Lấy thông tin suất chiếu
        MovieScreening screening = movieScreeningRepository.findByIdWithDetails(screeningId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu với ID: " + screeningId));

        // Lấy tất cả ghế của phòng chiếu
        List<Seat> seats = seatRepository.findByHallIdOrderByRowAndNumber(screening.getCinemaHall().getHallId());

        // Lấy danh sách ghế đã được đặt
        List<BookingSeat> bookedSeats = bookingSeatRepository.findBookedSeatsByScreeningId(screeningId);
        Set<Long> bookedSeatIds = bookedSeats.stream()
                .map(bs -> bs.getSeat().getSeatId())
                .collect(Collectors.toSet());

        // Tạo response DTO
        ScreeningDetailResponseDTO dto = new ScreeningDetailResponseDTO();
        dto.setScreeningId(screening.getScreeningId());
        dto.setMovieTitle(screening.getMovie().getTitle());
        dto.setDurationMinutes(screening.getMovie().getDurationMinutes());
        dto.setCinemaName(screening.getCinemaHall().getCinema().getName());
        dto.setCinemaAddress(screening.getCinemaHall().getCinema().getAddress());
        dto.setHallName(screening.getCinemaHall().getHallName());
        dto.setScreeningDate(screening.getScreeningDate());
        dto.setStartTime(screening.getStartTime());
        dto.setEndTime(screening.getEndTime());
        dto.setScreeningType(screening.getScreeningType().name());
        dto.setScreeningTypeDisplay(screening.getScreeningType().getDisplayName());
        dto.setAvailableSeats(screening.getAvailableSeats());
        dto.setTotalSeats(screening.getCinemaHall().getTotalSeats());

        // Tính giá cho các loại ghế
        List<SeatTypePriceDTO> seatTypePrices = calculateSeatTypePrices(seats, screening.getPriceMultiplier());
        dto.setSeatTypePrices(seatTypePrices);

        // Convert seats sang DTO
        List<SeatDetailDTO> seatDTOs = seats.stream()
                .map(seat -> convertToSeatDetailDTO(seat, screening.getPriceMultiplier(), bookedSeatIds))
                .collect(Collectors.toList());
        dto.setSeats(seatDTOs);

        return dto;
    }

    /**
     * Tính giá cho các loại ghế khác nhau
     */
    private List<SeatTypePriceDTO> calculateSeatTypePrices(List<Seat> seats, BigDecimal priceMultiplier) {
        Map<SeatType, BigDecimal> seatTypePriceMap = new LinkedHashMap<>();

        for (Seat seat : seats) {
            if (!seatTypePriceMap.containsKey(seat.getSeatType())) {
                seatTypePriceMap.put(seat.getSeatType(), seat.getBasePrice());
            }
        }

        List<SeatTypePriceDTO> result = new ArrayList<>();
        for (Map.Entry<SeatType, BigDecimal> entry : seatTypePriceMap.entrySet()) {
            BigDecimal basePrice = entry.getValue();
            BigDecimal finalPrice = basePrice.multiply(priceMultiplier);

            SeatTypePriceDTO dto = new SeatTypePriceDTO();
            dto.setSeatType(entry.getKey().name());
            dto.setSeatTypeDisplay(entry.getKey().getDisplayName());
            dto.setBasePrice(basePrice);
            dto.setFinalPrice(finalPrice);
            result.add(dto);
        }

        return result;
    }

    /**
     * Convert Seat entity sang SeatDetailDTO
     */
    private SeatDetailDTO convertToSeatDetailDTO(Seat seat, BigDecimal priceMultiplier, Set<Long> bookedSeatIds) {
        SeatDetailDTO dto = new SeatDetailDTO();
        dto.setSeatId(seat.getSeatId());
        dto.setRowLabel(seat.getRowLabel());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setSeatLabel(seat.getRowLabel() + seat.getSeatNumber());
        dto.setSeatType(seat.getSeatType().name());
        dto.setSeatTypeDisplay(seat.getSeatType().getDisplayName());
        dto.setBasePrice(seat.getBasePrice());
        dto.setFinalPrice(seat.getBasePrice().multiply(priceMultiplier));

        // Kiểm tra trạng thái ghế
        if (bookedSeatIds.contains(seat.getSeatId())) {
            dto.setStatus("BOOKED");
        } else {
            dto.setStatus("AVAILABLE");
        }

        return dto;
    }
}
