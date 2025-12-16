package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.example.storyreading.ibanking.entity.SavingTerm;

import java.math.BigDecimal;

public class UpdateSavingTermRateRequest {

    @NotNull(message = "Term type is required")
    private SavingTerm termType;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.0", message = "Interest rate must be greater than or equal to 0")
    private BigDecimal interestRate;

    public UpdateSavingTermRateRequest() {
    }

    public SavingTerm getTermType() {
        return termType;
    }

    public void setTermType(SavingTerm termType) {
        this.termType = termType;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}

