package org.example.storyreading.ibanking.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class TransferResponse {

    private Long transactionId;
    private String transactionCode;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private String receiverUserFullName;
    private BigDecimal amount;
    private String description;
    private BigDecimal senderNewBalance;
    private BigDecimal receiverNewBalance;
    private Instant transactionTime;
    private String status;

    // Constructors
    public TransferResponse() {
    }

    public TransferResponse(Long transactionId, String transactionCode, String senderAccountNumber,
                           String receiverAccountNumber, BigDecimal amount, String description,
                           BigDecimal senderNewBalance, BigDecimal receiverNewBalance,
                           Instant transactionTime, String status, String receiverUserFullName) {
        this.transactionId = transactionId;
        this.transactionCode = transactionCode;
        this.senderAccountNumber = senderAccountNumber;
        this.receiverAccountNumber = receiverAccountNumber;
        this.amount = amount;
        this.description = description;
        this.senderNewBalance = senderNewBalance;
        this.receiverNewBalance = receiverNewBalance;
        this.transactionTime = transactionTime;
        this.status = status;
        this.receiverUserFullName = receiverUserFullName;
    }

    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

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

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(String receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
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

    public BigDecimal getSenderNewBalance() {
        return senderNewBalance;
    }

    public void setSenderNewBalance(BigDecimal senderNewBalance) {
        this.senderNewBalance = senderNewBalance;
    }

    public BigDecimal getReceiverNewBalance() {
        return receiverNewBalance;
    }

    public void setReceiverNewBalance(BigDecimal receiverNewBalance) {
        this.receiverNewBalance = receiverNewBalance;
    }

    public Instant getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Instant transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getReceiverUserFullName() {
        return receiverUserFullName;
    }
    public void setReceiverUserFullName(String receiverUserFullName) {
        this.receiverUserFullName = receiverUserFullName;
    }
}

