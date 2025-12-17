package org.example.storyreading.ibanking.dto;

public class OtpResponse {
    private String transactionCode;
    private String message;

    public OtpResponse() {
    }

    public OtpResponse(String transactionCode, String message) {
        this.transactionCode = transactionCode;
        this.message = message;
    }

    // Getters and Setters
    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

