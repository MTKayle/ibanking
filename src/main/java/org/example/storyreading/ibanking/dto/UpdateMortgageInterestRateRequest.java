package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UpdateMortgageInterestRateRequest {

    @NotNull(message = "Rate ID không được để trống")
    private Long rateId;

    @NotNull(message = "Lãi suất không được để trống")
    @DecimalMin(value = "0.0001", message = "Lãi suất phải lớn hơn 0")
    private BigDecimal interestRate;

    private String description;

    // Constructors
    public UpdateMortgageInterestRateRequest() {
    }

    public UpdateMortgageInterestRateRequest(Long rateId, BigDecimal interestRate, String description) {
        this.rateId = rateId;
        this.interestRate = interestRate;
        this.description = description;
    }

    // Getters and Setters
    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
