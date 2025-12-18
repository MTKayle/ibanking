package org.example.storyreading.ibanking.entity;

public enum Language {
    VIETNAMESE("Tiếng Việt"),
    ENGLISH("Tiếng Anh"),
    CHINESE("Tiếng Trung"),
    KOREAN("Tiếng Hàn"),
    JAPANESE("Tiếng Nhật"),
    THAI("Tiếng Thái"),
    FRENCH("Tiếng Pháp"),
    SPANISH("Tiếng Tây Ban Nha");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

