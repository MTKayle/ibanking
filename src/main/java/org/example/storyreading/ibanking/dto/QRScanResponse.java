package org.example.storyreading.ibanking.dto;

import java.math.BigDecimal;

public class QRScanResponse {

    private String accountNumber;
    private String accountHolderName;
    private String bankBin;
    private String bankCode;
    private String bankName;
    private BigDecimal amount;
    private String description;
    private Long userId;
    private String accountType;

    public QRScanResponse() {}

    public QRScanResponse(String accountNumber, String accountHolderName,
                         String bankBin, String bankCode, String bankName,
                         BigDecimal amount, String description, Long userId, String accountType) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.bankBin = bankBin;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.amount = amount;
        this.description = description;
        this.userId = userId;
        this.accountType = accountType;
    }

    // Getters and setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getBankBin() {
        return bankBin;
    }

    public void setBankBin(String bankBin) {
        this.bankBin = bankBin;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}

