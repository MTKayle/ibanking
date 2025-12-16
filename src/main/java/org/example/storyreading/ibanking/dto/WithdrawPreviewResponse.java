package org.example.storyreading.ibanking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WithdrawPreviewResponse {

    private String savingBookNumber;
    private BigDecimal principalAmount;        // Tiền gốc
    private BigDecimal appliedInterestRate;    // Lãi suất áp dụng
    private BigDecimal interestEarned;         // Lãi ước tính
    private BigDecimal totalAmount;            // Tổng tiền ước tính nhận
    private LocalDate openedDate;
    private LocalDate withdrawDate;            // Ngày rút dự kiến (hôm nay)
    private int daysHeld;                      // Số ngày gửi
    private boolean isEarlyWithdrawal;         // Rút trước hạn?
    private String message;                    // Message cảnh báo nếu rút trước hạn

    public WithdrawPreviewResponse() {
    }

    // Getters and Setters
    public String getSavingBookNumber() {
        return savingBookNumber;
    }

    public void setSavingBookNumber(String savingBookNumber) {
        this.savingBookNumber = savingBookNumber;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public BigDecimal getAppliedInterestRate() {
        return appliedInterestRate;
    }

    public void setAppliedInterestRate(BigDecimal appliedInterestRate) {
        this.appliedInterestRate = appliedInterestRate;
    }

    public BigDecimal getInterestEarned() {
        return interestEarned;
    }

    public void setInterestEarned(BigDecimal interestEarned) {
        this.interestEarned = interestEarned;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getOpenedDate() {
        return openedDate;
    }

    public void setOpenedDate(LocalDate openedDate) {
        this.openedDate = openedDate;
    }

    public LocalDate getWithdrawDate() {
        return withdrawDate;
    }

    public void setWithdrawDate(LocalDate withdrawDate) {
        this.withdrawDate = withdrawDate;
    }

    public int getDaysHeld() {
        return daysHeld;
    }

    public void setDaysHeld(int daysHeld) {
        this.daysHeld = daysHeld;
    }

    public boolean isEarlyWithdrawal() {
        return isEarlyWithdrawal;
    }

    public void setEarlyWithdrawal(boolean earlyWithdrawal) {
        isEarlyWithdrawal = earlyWithdrawal;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

