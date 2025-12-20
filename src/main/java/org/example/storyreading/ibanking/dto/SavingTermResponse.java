package org.example.storyreading.ibanking.dto;

import java.math.BigDecimal;

/**
 * DTO để trả về thông tin kỳ hạn tiết kiệm
 */
public class SavingTermResponse {

    private Long termId;
    private String termType;
    private Integer months;
    private String displayName;
    private BigDecimal interestRate;
    private String updatedBy;
    private String updatedAt;

    // Constructors
    public SavingTermResponse() {
    }

    // Getters and Setters
    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

