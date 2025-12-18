package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movie_screenings")
public class MovieScreening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screening_id")
    private Long screeningId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private CinemaHall cinemaHall;

    @NotNull
    @Column(name = "screening_date", nullable = false)
    private LocalDate screeningDate;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "screening_type", nullable = false, length = 20)
    private ScreeningType screeningType; // 2D, 3D, IMAX...

    @NotNull
    @Column(name = "price_multiplier", nullable = false, precision = 3, scale = 2)
    private BigDecimal priceMultiplier = BigDecimal.ONE; // Hệ số nhân giá (VD: 3D = 1.5, IMAX = 2.0)

    @Column(name = "available_seats")
    private Integer availableSeats;

    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    // Constructors
    public MovieScreening() {
    }

    // Getters and Setters
    public Long getScreeningId() {
        return screeningId;
    }

    public void setScreeningId(Long screeningId) {
        this.screeningId = screeningId;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public CinemaHall getCinemaHall() {
        return cinemaHall;
    }

    public void setCinemaHall(CinemaHall cinemaHall) {
        this.cinemaHall = cinemaHall;
    }

    public LocalDate getScreeningDate() {
        return screeningDate;
    }

    public void setScreeningDate(LocalDate screeningDate) {
        this.screeningDate = screeningDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public ScreeningType getScreeningType() {
        return screeningType;
    }

    public void setScreeningType(ScreeningType screeningType) {
        this.screeningType = screeningType;
    }

    public BigDecimal getPriceMultiplier() {
        return priceMultiplier;
    }

    public void setPriceMultiplier(BigDecimal priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    // Helper method
    public LocalDateTime getScreeningDateTime() {
        return LocalDateTime.of(screeningDate, startTime);
    }
}

