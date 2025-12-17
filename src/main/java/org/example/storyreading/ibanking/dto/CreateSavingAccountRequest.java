package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.storyreading.ibanking.entity.SavingTerm;

import java.math.BigDecimal;

public class CreateSavingAccountRequest {

    @NotBlank(message = "Sender account number is required")
    private String senderAccountNumber; // Tài khoản checking để trừ tiền

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount; // Số tiền gửi

    @NotNull(message = "Term is required")
    private SavingTerm term; // Kỳ hạn

    // Getters and Setters
    public String getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public void setSenderAccountNumber(String senderAccountNumber) {
        this.senderAccountNumber = senderAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public SavingTerm getTerm() {
        return term;
    }

    public void setTerm(SavingTerm term) {
        this.term = term;
    }
}

