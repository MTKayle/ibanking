package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VerifyOtpRequest {

    @NotBlank(message = "Transaction code is required")
    private String transactionCode;


    public VerifyOtpRequest() {
    }

    public VerifyOtpRequest(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    // Getters and Setters
    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

}

