package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.NotBlank;

public class FcmTokenRequest {

    @NotBlank(message = "FCM token không được để trống")
    private String fcmToken;

    public FcmTokenRequest() {}

    public FcmTokenRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}

