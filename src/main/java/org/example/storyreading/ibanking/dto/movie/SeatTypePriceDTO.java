package org.example.storyreading.ibanking.dto.movie;

import java.math.BigDecimal;

public class SeatTypePriceDTO {
    private String seatType;
    private String seatTypeDisplay;
    private BigDecimal basePrice;
    private BigDecimal finalPrice;

    // Constructors
    public SeatTypePriceDTO() {
    }

    public SeatTypePriceDTO(String seatType, String seatTypeDisplay,
                           BigDecimal basePrice, BigDecimal finalPrice) {
        this.seatType = seatType;
        this.seatTypeDisplay = seatTypeDisplay;
        this.basePrice = basePrice;
        this.finalPrice = finalPrice;
    }

    // Getters and Setters
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
}

