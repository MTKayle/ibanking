package org.example.storyreading.ibanking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WithdrawSavingResponse {

    private String savingBookNumber;
    private BigDecimal principalAmount;        // Tiền gốc
    private BigDecimal appliedInterestRate;    // Lãi suất áp dụng
    private BigDecimal interestEarned;         // Lãi thực nhận
    private BigDecimal totalAmount;            // Tổng tiền nhận
    private String checkingAccountNumber;      // Tài khoản nhận tiền
    private BigDecimal newCheckingBalance;     // Số dư mới của tài khoản checking
    private LocalDate openedDate;
    private LocalDate closedDate;
    private int daysHeld;                      // Số ngày gửi
    private String transactionCode;
    private String message;

    public WithdrawSavingResponse() {
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

    public String getCheckingAccountNumber() {
        return checkingAccountNumber;
    }

    public void setCheckingAccountNumber(String checkingAccountNumber) {
        this.checkingAccountNumber = checkingAccountNumber;
    }

    public BigDecimal getNewCheckingBalance() {
        return newCheckingBalance;
    }

    public void setNewCheckingBalance(BigDecimal newCheckingBalance) {
        this.newCheckingBalance = newCheckingBalance;
    }

    public LocalDate getOpenedDate() {
        return openedDate;
    }

    public void setOpenedDate(LocalDate openedDate) {
        this.openedDate = openedDate;
    }

    public LocalDate getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(LocalDate closedDate) {
        this.closedDate = closedDate;
    }

    public int getDaysHeld() {
        return daysHeld;
    }

    public void setDaysHeld(int daysHeld) {
        this.daysHeld = daysHeld;
    }

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

