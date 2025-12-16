package org.example.storyreading.ibanking.dto;

import java.math.BigDecimal;

public class SavingAccountDetailResponse {

    private Long savingId;
    private String savingBookNumber;
    private String accountNumber;
    private BigDecimal balance;
    private String term;
    private int termMonths;
    private BigDecimal interestRate;
    private String openedDate;
    private String maturityDate;
    private String status;
    private Long userId;
    private String userFullName;

    // Thông tin lãi ước tính khi đáo hạn
    private BigDecimal estimatedInterestAtMaturity;  // Lãi ước tính khi đáo hạn
    private BigDecimal estimatedTotalAtMaturity;     // Tổng tiền ước tính khi đáo hạn
    private int daysUntilMaturity;                   // Số ngày còn lại đến đáo hạn
    private int totalDaysOfTerm;                     // Tổng số ngày của kỳ hạn

    public SavingAccountDetailResponse() {
    }

    // Getters and Setters
    public Long getSavingId() {
        return savingId;
    }

    public void setSavingId(Long savingId) {
        this.savingId = savingId;
    }

    public String getSavingBookNumber() {
        return savingBookNumber;
    }

    public void setSavingBookNumber(String savingBookNumber) {
        this.savingBookNumber = savingBookNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public String getOpenedDate() {
        return openedDate;
    }

    public void setOpenedDate(String openedDate) {
        this.openedDate = openedDate;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public BigDecimal getEstimatedInterestAtMaturity() {
        return estimatedInterestAtMaturity;
    }

    public void setEstimatedInterestAtMaturity(BigDecimal estimatedInterestAtMaturity) {
        this.estimatedInterestAtMaturity = estimatedInterestAtMaturity;
    }

    public BigDecimal getEstimatedTotalAtMaturity() {
        return estimatedTotalAtMaturity;
    }

    public void setEstimatedTotalAtMaturity(BigDecimal estimatedTotalAtMaturity) {
        this.estimatedTotalAtMaturity = estimatedTotalAtMaturity;
    }

    public int getDaysUntilMaturity() {
        return daysUntilMaturity;
    }

    public void setDaysUntilMaturity(int daysUntilMaturity) {
        this.daysUntilMaturity = daysUntilMaturity;
    }

    public int getTotalDaysOfTerm() {
        return totalDaysOfTerm;
    }

    public void setTotalDaysOfTerm(int totalDaysOfTerm) {
        this.totalDaysOfTerm = totalDaysOfTerm;
    }
}

