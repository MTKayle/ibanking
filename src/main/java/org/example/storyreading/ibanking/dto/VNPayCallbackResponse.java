package org.example.storyreading.ibanking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VNPayCallbackResponse {

    private boolean success;
    private String message;
    private String txnRef;
    private String vnpTransactionNo;
    private BigDecimal amount;
    private String accountNumber;
    private String bankCode;
    private String status;
    private LocalDateTime payDate;

    public VNPayCallbackResponse() {}

    public VNPayCallbackResponse(boolean success, String message, String txnRef, String vnpTransactionNo,
                                  BigDecimal amount, String accountNumber, String bankCode,
                                  String status, LocalDateTime payDate) {
        this.success = success;
        this.message = message;
        this.txnRef = txnRef;
        this.vnpTransactionNo = vnpTransactionNo;
        this.amount = amount;
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
        this.status = status;
        this.payDate = payDate;
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

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getVnpTransactionNo() {
        return vnpTransactionNo;
    }

    public void setVnpTransactionNo(String vnpTransactionNo) {
        this.vnpTransactionNo = vnpTransactionNo;
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

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPayDate() {
        return payDate;
    }

    public void setPayDate(LocalDateTime payDate) {
        this.payDate = payDate;
    }
}

