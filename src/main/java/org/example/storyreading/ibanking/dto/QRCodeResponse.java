package org.example.storyreading.ibanking.dto;

import java.math.BigDecimal;

public class QRCodeResponse {

    private String qrCodeBase64;
    private String accountNumber;
    private String accountHolderName;
    private String bankCode;
    private String bankName;
    private BigDecimal amount;
    private String description;
    private String qrContent;

    public QRCodeResponse() {}

    public QRCodeResponse(String qrCodeBase64, String accountNumber, String accountHolderName,
                         String bankCode, String bankName, BigDecimal amount, String description, String qrContent) {
        this.qrCodeBase64 = qrCodeBase64;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.amount = amount;
        this.description = description;
        this.qrContent = qrContent;
    }

    public String getQrCodeBase64() {
        return qrCodeBase64;
    }

    public void setQrCodeBase64(String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
    }

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

    public String getQrContent() {
        return qrContent;
    }

    public void setQrContent(String qrContent) {
        this.qrContent = qrContent;
    }
}

