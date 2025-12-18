package org.example.storyreading.ibanking.entity;

public enum BookingStatus {
    PENDING("Chờ thanh toán"),
    CONFIRMED("Đã xác nhận"),
    CANCELLED("Đã hủy"),
    EXPIRED("Đã hết hạn"),
    COMPLETED("Hoàn thành");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

