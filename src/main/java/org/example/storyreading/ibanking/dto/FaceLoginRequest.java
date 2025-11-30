package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.NotBlank;

public class FaceLoginRequest {

    @NotBlank(message = "Phone number is required")
    private String phone;

    // Note: The actual face photo will be sent as MultipartFile in the controller
    // This DTO is just for the phone field if needed

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

