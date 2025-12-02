package org.example.storyreading.ibanking.dto;

import java.math.BigDecimal;

public class CheckingAccountInfoResponse {
    private String accountNumber;
    private Long checkingId;
    private BigDecimal balance;
    private Long userId;
    private String userPhone;

    public CheckingAccountInfoResponse() {}

    public CheckingAccountInfoResponse(String accountNumber, Long checkingId, BigDecimal balance, Long userId, String userPhone) {
        this.accountNumber = accountNumber;
        this.checkingId = checkingId;
        this.balance = balance;
        this.userId = userId;
        this.userPhone = userPhone;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getCheckingId() {
        return checkingId;
    }

    public void setCheckingId(Long checkingId) {
        this.checkingId = checkingId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}

