package org.example.storyreading.ibanking.dto;

import org.example.storyreading.ibanking.entity.MortgageAccount;
import org.example.storyreading.ibanking.entity.CollateralType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MortgageAccountResponse {

    private Long mortgageId;
    private String accountNumber;
    private String customerName;
    private String customerPhone;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private Integer termMonths;
    private LocalDate startDate;
    private MortgageAccount.MortgageStatus status;
    private CollateralType collateralType;
    private String collateralDescription;
    private String cccdFrontUrl;
    private String cccdBackUrl;
    private String collateralDocumentUrls;
    private MortgageAccount.PaymentFrequency paymentFrequency;
    private String rejectionReason;
    private LocalDate createdDate;
    private LocalDate approvalDate;
    private BigDecimal remainingBalance;
    private BigDecimal earlySettlementAmount; // Tổng tiền tất toán sớm = gốc còn lại + lãi các kỳ chưa thanh toán
    private List<PaymentScheduleResponse> paymentSchedules;

    // Getters and setters
    public Long getMortgageId() {
        return mortgageId;
    }

    public void setMortgageId(Long mortgageId) {
        this.mortgageId = mortgageId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public MortgageAccount.MortgageStatus getStatus() {
        return status;
    }

    public void setStatus(MortgageAccount.MortgageStatus status) {
        this.status = status;
    }

    public CollateralType getCollateralType() {
        return collateralType;
    }

    public void setCollateralType(CollateralType collateralType) {
        this.collateralType = collateralType;
    }

    public String getCollateralDescription() {
        return collateralDescription;
    }

    public void setCollateralDescription(String collateralDescription) {
        this.collateralDescription = collateralDescription;
    }

    public String getCccdFrontUrl() {
        return cccdFrontUrl;
    }

    public void setCccdFrontUrl(String cccdFrontUrl) {
        this.cccdFrontUrl = cccdFrontUrl;
    }

    public String getCccdBackUrl() {
        return cccdBackUrl;
    }

    public void setCccdBackUrl(String cccdBackUrl) {
        this.cccdBackUrl = cccdBackUrl;
    }

    public String getCollateralDocumentUrls() {
        return collateralDocumentUrls;
    }

    public void setCollateralDocumentUrls(String collateralDocumentUrls) {
        this.collateralDocumentUrls = collateralDocumentUrls;
    }

    public MortgageAccount.PaymentFrequency getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setPaymentFrequency(MortgageAccount.PaymentFrequency paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public BigDecimal getEarlySettlementAmount() {
        return earlySettlementAmount;
    }

    public void setEarlySettlementAmount(BigDecimal earlySettlementAmount) {
        this.earlySettlementAmount = earlySettlementAmount;
    }

    public List<PaymentScheduleResponse> getPaymentSchedules() {
        return paymentSchedules;
    }

    public void setPaymentSchedules(List<PaymentScheduleResponse> paymentSchedules) {
        this.paymentSchedules = paymentSchedules;
    }
}
