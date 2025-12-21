package org.example.storyreading.ibanking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VNPayDepositResponse {

    private boolean success;
    private String message;
    private String paymentUrl;
    private String txnRef;
    private BigDecimal amount;
    private String accountNumber;
    private LocalDateTime createdAt;

    public VNPayDepositResponse() {}

    public VNPayDepositResponse(boolean success, String message, String paymentUrl, String txnRef,
                                 BigDecimal amount, String accountNumber, LocalDateTime createdAt) {
        this.success = success;
        this.message = message;
        this.paymentUrl = paymentUrl;
        this.txnRef = txnRef;
        this.amount = amount;
        this.accountNumber = accountNumber;
        this.createdAt = createdAt;
    }

    // Static factory methods
    public static VNPayDepositResponse success(String paymentUrl, String txnRef, BigDecimal amount,
                                                String accountNumber, LocalDateTime createdAt) {
        return new VNPayDepositResponse(true, "Tạo link thanh toán thành công", paymentUrl, txnRef,
                                        amount, accountNumber, createdAt);
    }

    public static VNPayDepositResponse error(String message) {
        return new VNPayDepositResponse(false, message, null, null, null, null, null);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

