package org.example.storyreading.ibanking.entity;

public enum MovieGenre {
    ACTION("Hành động"),
    COMEDY("Hài"),
    DRAMA("Chính kịch"),
    HORROR("Kinh dị"),
    ROMANCE("Lãng mạn"),
    SCI_FI("Khoa học viễn tưởng"),
    THRILLER("Ly kỳ"),
    ANIMATION("Hoạt hình"),
    ADVENTURE("Phiêu lưu"),
    FANTASY("Giả tưởng"),
    CRIME("Tội phạm"),
    DOCUMENTARY("Tài liệu"),
    MYSTERY("Bí ẩn"),
    WAR("Chiến tranh"),
    MUSICAL("Nhạc kịch"),
    FAMILY("Gia đình");

    private final String displayName;

    MovieGenre(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
