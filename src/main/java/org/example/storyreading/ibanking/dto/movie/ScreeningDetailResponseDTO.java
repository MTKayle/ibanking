package org.example.storyreading.ibanking.dto.movie;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ScreeningDetailResponseDTO {
    private Long screeningId;
    private String movieTitle;
    private Integer durationMinutes;
    private String cinemaName;
    private String cinemaAddress;
    private String hallName;
    private LocalDate screeningDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String screeningType;
    private String screeningTypeDisplay;
    private Integer availableSeats;
    private Integer totalSeats;
    private List<SeatTypePriceDTO> seatTypePrices; // Danh sách loại ghế và giá
    private List<SeatDetailDTO> seats; // Tất cả ghế xếp theo hàng

    // Constructors
    public ScreeningDetailResponseDTO() {
    }

    // Getters and Setters
    public Long getScreeningId() {
        return screeningId;
    }

    public void setScreeningId(Long screeningId) {
        this.screeningId = screeningId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getCinemaAddress() {
        return cinemaAddress;
    }

    public void setCinemaAddress(String cinemaAddress) {
        this.cinemaAddress = cinemaAddress;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
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

    public String getScreeningType() {
        return screeningType;
    }

    public void setScreeningType(String screeningType) {
        this.screeningType = screeningType;
    }

    public String getScreeningTypeDisplay() {
        return screeningTypeDisplay;
    }

    public void setScreeningTypeDisplay(String screeningTypeDisplay) {
        this.screeningTypeDisplay = screeningTypeDisplay;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public List<SeatTypePriceDTO> getSeatTypePrices() {
        return seatTypePrices;
    }

    public void setSeatTypePrices(List<SeatTypePriceDTO> seatTypePrices) {
        this.seatTypePrices = seatTypePrices;
    }

    public List<SeatDetailDTO> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatDetailDTO> seats) {
        this.seats = seats;
    }
}

