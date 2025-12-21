package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class VNPayDepositRequest {

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "10000", message = "Số tiền tối thiểu là 10,000 VND")
    private BigDecimal amount;

    private String orderInfo;

    private String bankCode; // NCB, VNPAYQR, etc.

    private String language = "vn"; // vn hoặc en

    public VNPayDepositRequest() {}

    public VNPayDepositRequest(BigDecimal amount, String orderInfo, String bankCode, String language) {
        this.amount = amount;
        this.orderInfo = orderInfo;
        this.bankCode = bankCode;
        this.language = language;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}

