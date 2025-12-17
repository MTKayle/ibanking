package org.example.storyreading.ibanking.entity;

public enum CollateralType {
    HOUSE("Nhà"),
    LAND("Đất"),
    CAR("Xe");

    private final String displayName;

    CollateralType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

