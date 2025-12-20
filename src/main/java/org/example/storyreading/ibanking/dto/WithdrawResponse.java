package org.example.storyreading.ibanking.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class WithdrawResponse {

    private String accountNumber;
    private BigDecimal newBalance;
    private BigDecimal withdrawAmount;
    private String description;
    private Instant timestamp;
    private String message;

    public WithdrawResponse() {
    }

    public WithdrawResponse(String accountNumber, BigDecimal newBalance, BigDecimal withdrawAmount,
                          String description, Instant timestamp, String message) {
        this.accountNumber = accountNumber;
        this.newBalance = newBalance;
        this.withdrawAmount = withdrawAmount;
        this.description = description;
        this.timestamp = timestamp;
        this.message = message;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(BigDecimal newBalance) {
        this.newBalance = newBalance;
    }

    public BigDecimal getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(BigDecimal withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

