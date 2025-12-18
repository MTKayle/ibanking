package org.example.storyreading.ibanking.entity;

public enum ScreeningType {
    TWO_D("2D"),
    THREE_D("3D"),
    IMAX("IMAX"),
    IMAX_3D("IMAX 3D"),
    FOUR_DX("4DX"),
    SCREEN_X("ScreenX");

    private final String displayName;

    ScreeningType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

