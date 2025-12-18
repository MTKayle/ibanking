package org.example.storyreading.ibanking.dto.movie;

import java.math.BigDecimal;

public class SeatDetailDTO {
    private Long seatId;
    private String rowLabel;
    private Integer seatNumber;
    private String seatLabel; // VD: "A1", "B5"
    private String seatType;
    private String seatTypeDisplay;
    private BigDecimal basePrice;
    private BigDecimal finalPrice; // Giá sau khi nhân với hệ số screening
    private String status; // AVAILABLE, BOOKED, RESERVED

    // Constructors
    public SeatDetailDTO() {
    }

    public SeatDetailDTO(Long seatId, String rowLabel, Integer seatNumber, String seatLabel,
                        String seatType, String seatTypeDisplay, BigDecimal basePrice,
                        BigDecimal finalPrice, String status) {
        this.seatId = seatId;
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
        this.seatLabel = seatLabel;
        this.seatType = seatType;
        this.seatTypeDisplay = seatTypeDisplay;
        this.basePrice = basePrice;
        this.finalPrice = finalPrice;
        this.status = status;
    }

    // Getters and Setters
    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public void setRowLabel(String rowLabel) {
        this.rowLabel = rowLabel;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatLabel() {
        return seatLabel;
    }

    public void setSeatLabel(String seatLabel) {
        this.seatLabel = seatLabel;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public String getSeatTypeDisplay() {
        return seatTypeDisplay;
    }

    public void setSeatTypeDisplay(String seatTypeDisplay) {
        this.seatTypeDisplay = seatTypeDisplay;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

