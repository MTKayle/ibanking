package org.example.storyreading.ibanking.entity;

public enum UtilityBillStatus {
    UNPAID("Chưa thanh toán"),
    PAID("Đã thanh toán"),
    OVERDUE("Quá hạn"),
    CANCELLED("Đã hủy");

    private final String displayName;

    UtilityBillStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

