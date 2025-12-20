package org.example.storyreading.ibanking.entity;

public enum UtilityBillType {
    ELECTRICITY("Tiền điện"),
    WATER("Tiền nước"),
    INTERNET("Internet");

    private final String displayName;

    UtilityBillType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

