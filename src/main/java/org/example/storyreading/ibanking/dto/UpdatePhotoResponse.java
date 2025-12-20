package org.example.storyreading.ibanking.dto;

public class UpdatePhotoResponse {

    private Long userId;
    private String phone;
    private String photoUrl;
    private String message;

    public UpdatePhotoResponse() {
    }

    public UpdatePhotoResponse(Long userId, String phone, String photoUrl, String message) {
        this.userId = userId;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

