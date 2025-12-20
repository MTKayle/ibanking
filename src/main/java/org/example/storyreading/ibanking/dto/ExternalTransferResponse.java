package org.example.storyreading.ibanking.dto;

import java.math.BigDecimal;

/**
 * DTO response cho chuyển tiền ngoài ngân hàng
 */
public class ExternalTransferResponse {

    private String transactionCode;
    private String senderAccountNumber;
    private String receiverBankName;
    private String receiverBankBin;
    private String receiverAccountNumber;
    private String receiverName;
    private BigDecimal amount;
    private String description;
    private String status;
    private BigDecimal senderNewBalance;
    private String message;
    private String createdAt;
    private String completedAt;

    // Constructors
    public ExternalTransferResponse() {
    }

    // Getters and Setters
    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public void setSenderAccountNumber(String senderAccountNumber) {
        this.senderAccountNumber = senderAccountNumber;
    }

    public String getReceiverBankName() {
        return receiverBankName;
    }

    public void setReceiverBankName(String receiverBankName) {
        this.receiverBankName = receiverBankName;
    }

    public String getReceiverBankBin() {
        return receiverBankBin;
    }

    public void setReceiverBankBin(String receiverBankBin) {
        this.receiverBankBin = receiverBankBin;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(String receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getSenderNewBalance() {
        return senderNewBalance;
    }

    public void setSenderNewBalance(BigDecimal senderNewBalance) {
        this.senderNewBalance = senderNewBalance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
}

