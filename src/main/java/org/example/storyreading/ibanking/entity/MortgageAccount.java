package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mortgage_accounts")
public class MortgageAccount {

    @Id
    @Column(name = "mortgage_id")
    private Long mortgageId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "mortgage_id", referencedColumnName = "account_id")
    private Account account;

    @NotNull
    @Column(name = "principal_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal principalAmount = BigDecimal.ZERO;

    @Column(name = "interest_rate", precision = 10, scale = 4)
    private BigDecimal interestRate = BigDecimal.ZERO;

    @Column(name = "term_months")
    private Integer termMonths;

    @Column(name = "start_date")
    private LocalDate startDate;

    // Trạng thái tài khoản vay
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private MortgageStatus status = MortgageStatus.PENDING_APPRAISAL;

    // Loại tài sản thế chấp
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "collateral_type", length = 100)
    private CollateralType collateralType;

    // Mô tả tài sản
    @Column(name = "collateral_description", columnDefinition = "TEXT")
    private String collateralDescription;

    // URL ảnh CCCD mặt trước
    @Column(name = "cccd_front_url", columnDefinition = "TEXT")
    private String cccdFrontUrl;

    // URL ảnh CCCD mặt sau
    @Column(name = "cccd_back_url", columnDefinition = "TEXT")
    private String cccdBackUrl;

    // URL ảnh giấy tờ tài sản (có thể nhiều, lưu dạng JSON array hoặc phân cách bằng dấu phẩy)
    @Column(name = "collateral_document_urls", columnDefinition = "TEXT")
    private String collateralDocumentUrls;

    // Loại kỳ hạn thanh toán: MONTHLY (1 tháng) hoặc BI_WEEKLY (2 tuần)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_frequency", nullable = false, length = 20)
    private PaymentFrequency paymentFrequency = PaymentFrequency.MONTHLY;

    // Lý do từ chối (nếu có)
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // Ngày tạo yêu cầu
    @Column(name = "created_date")
    private LocalDate createdDate;

    // Ngày phê duyệt/từ chối
    @Column(name = "approval_date")
    private LocalDate approvalDate;

    public enum MortgageStatus {
        PENDING_APPRAISAL,  // Chờ thẩm định
        APPROVED,           // Đã phê duyệt
        REJECTED,           // Từ chối
        ACTIVE,             // Đang hoạt động
        COMPLETED,          // Đã hoàn thành
        DEFAULTED           // Quá hạn nghiêm trọng
    }

    public enum PaymentFrequency {
        MONTHLY,    // 1 tháng
        BI_WEEKLY   // 2 tuần
    }

//    public enum CollateralType {
//        REAL_ESTATE, // Bất động sản
//        VEHICLE,     // Phương tiện giao thông
//        MACHINERY,   // Máy móc thiết bị
//        OTHER        // Loại khác
//    }

    public MortgageAccount() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.principalAmount == null) this.principalAmount = BigDecimal.ZERO;
        if (this.interestRate == null) this.interestRate = BigDecimal.ZERO;
        if (this.status == null) this.status = MortgageStatus.PENDING_APPRAISAL;
        if (this.paymentFrequency == null) this.paymentFrequency = PaymentFrequency.MONTHLY;
        if (this.createdDate == null) this.createdDate = LocalDate.now();
    }

    // Getters and setters
    public Long getMortgageId() {
        return mortgageId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public MortgageStatus getStatus() {
        return status;
    }

    public void setStatus(MortgageStatus status) {
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

    public PaymentFrequency getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setPaymentFrequency(PaymentFrequency paymentFrequency) {
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
}
