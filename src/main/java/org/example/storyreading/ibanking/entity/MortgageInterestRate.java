package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "mortgage_interest_rates")
public class MortgageInterestRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rate_id")
    private Long rateId;

    @NotNull
    @Column(name = "min_months", nullable = false)
    private Integer minMonths;

    @Column(name = "max_months")
    private Integer maxMonths; // null nghĩa là không giới hạn (> 120 tháng)

    @NotNull
    @Column(name = "interest_rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "description", length = 100)
    private String description;

    public MortgageInterestRate() {
    }

    public MortgageInterestRate(Integer minMonths, Integer maxMonths, BigDecimal interestRate, String description) {
        this.minMonths = minMonths;
        this.maxMonths = maxMonths;
        this.interestRate = interestRate;
        this.description = description;
    }

    // Getters and setters
    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    public Integer getMinMonths() {
        return minMonths;
    }

    public void setMinMonths(Integer minMonths) {
        this.minMonths = minMonths;
    }

    public Integer getMaxMonths() {
        return maxMonths;
    }

    public void setMaxMonths(Integer maxMonths) {
        this.maxMonths = maxMonths;
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

