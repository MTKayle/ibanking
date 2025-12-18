package org.example.storyreading.ibanking.entity;

public enum SeatStatus {
    AVAILABLE("Còn trống"),
    BOOKED("Đã đặt"),
    RESERVED("Đang giữ chỗ"),
    MAINTENANCE("Bảo trì");

    private final String displayName;

    SeatStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

