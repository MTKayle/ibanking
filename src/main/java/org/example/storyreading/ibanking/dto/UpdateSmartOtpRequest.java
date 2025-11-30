package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateSmartOtpRequest {

    @NotBlank(message = "smatOTP must not be blank")
    @Size(max = 100, message = "smatOTP must not exceed 100 characters")
    private String smatOTP;

    public String getSmatOTP() {
        return smatOTP;
    }

    public void setSmatOTP(String smatOTP) {
        this.smatOTP = smatOTP;
    }
}

