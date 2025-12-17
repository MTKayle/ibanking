package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.NotBlank;

public class ConfirmWithdrawRequest {

    @NotBlank(message = "Saving book number is required")
    private String savingBookNumber;

    public ConfirmWithdrawRequest() {
    }

    public String getSavingBookNumber() {
        return savingBookNumber;
    }

    public void setSavingBookNumber(String savingBookNumber) {
        this.savingBookNumber = savingBookNumber;
    }
}

