package org.example.storyreading.ibanking.dto.movie;

import java.math.BigDecimal;

public class BookedSeatDetailDTO {
    private Long seatId;
    private String seatLabel; // VD: "A1", "B5"
    private String seatType;
    private String seatTypeDisplay;
    private BigDecimal price; // Giá đã thanh toán cho ghế này

    // Constructors
    public BookedSeatDetailDTO() {
    }

    public BookedSeatDetailDTO(Long seatId, String seatLabel, String seatType,
                              String seatTypeDisplay, BigDecimal price) {
        this.seatId = seatId;
        this.seatLabel = seatLabel;
        this.seatType = seatType;
        this.seatTypeDisplay = seatTypeDisplay;
        this.price = price;
    }

    // Getters and Setters
    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

