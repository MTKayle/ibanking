package org.example.storyreading.ibanking.dto.movie;

import org.example.storyreading.ibanking.entity.ScreeningType;

import java.math.BigDecimal;
import java.time.LocalTime;

public class ScreeningResponseDTO {
    private Long screeningId;
    private Long hallId;
    private String hallName;
    private LocalTime startTime;
    private LocalTime endTime;
    private ScreeningType screeningType;
    private String screeningTypeDisplay;
    private Integer availableSeats;
    private Integer totalSeats;
    private BigDecimal priceMultiplier;

    // Constructors
    public ScreeningResponseDTO() {
    }

    public ScreeningResponseDTO(Long screeningId, Long hallId, String hallName,
                                LocalTime startTime, LocalTime endTime,
                                ScreeningType screeningType, String screeningTypeDisplay,
                                Integer availableSeats, Integer totalSeats,
                                BigDecimal priceMultiplier) {
        this.screeningId = screeningId;
        this.hallId = hallId;
        this.hallName = hallName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.screeningType = screeningType;
        this.screeningTypeDisplay = screeningTypeDisplay;
        this.availableSeats = availableSeats;
        this.totalSeats = totalSeats;
        this.priceMultiplier = priceMultiplier;
    }

    // Getters and Setters
    public Long getScreeningId() {
        return screeningId;
    }

    public void setScreeningId(Long screeningId) {
        this.screeningId = screeningId;
    }

    public Long getHallId() {
        return hallId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
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

    public BigDecimal getPriceMultiplier() {
        return priceMultiplier;
    }

    public void setPriceMultiplier(BigDecimal priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }
}

