package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.NotNull;

public class UpdatePhotoRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    public UpdatePhotoRequest() {
    }

    public UpdatePhotoRequest(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
