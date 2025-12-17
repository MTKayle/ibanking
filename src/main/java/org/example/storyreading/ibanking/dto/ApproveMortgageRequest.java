package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class ApproveMortgageRequest {

    @NotNull(message = "ID tài khoản vay không được để trống")
    private Long mortgageId;

    @NotNull(message = "Số tiền vay không được để trống")
    @Positive(message = "Số tiền vay phải lớn hơn 0")
    private BigDecimal principalAmount;

    // Lãi suất là optional - nếu không cung cấp sẽ tự động lấy từ bảng lãi suất
    @Positive(message = "Lãi suất phải lớn hơn 0")
    private BigDecimal interestRate;

    @NotNull(message = "Số kỳ hạn không được để trống")
    @Positive(message = "Số kỳ hạn phải lớn hơn 0")
    private Integer termMonths;

    // Getters and setters
    public Long getMortgageId() {
        return mortgageId;
    }

    public void setMortgageId(Long mortgageId) {
        this.mortgageId = mortgageId;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }
}
