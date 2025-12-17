package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mortgage_payment_schedules")
public class MortgagePaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mortgage_id", referencedColumnName = "mortgage_id", nullable = false)
    private MortgageAccount mortgageAccount;

    @NotNull
    @Column(name = "period_number", nullable = false)
    private Integer periodNumber; // Kỳ thứ mấy

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate; // Ngày đến hạn

    @NotNull
    @Column(name = "principal_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal principalAmount = BigDecimal.ZERO; // Tiền gốc phải trả

    @NotNull
    @Column(name = "interest_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal interestAmount = BigDecimal.ZERO; // Tiền lãi phải trả

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO; // Tổng tiền phải trả (gốc + lãi)

    @Column(name = "penalty_amount", precision = 19, scale = 2)
    private BigDecimal penaltyAmount = BigDecimal.ZERO; // Lãi phạt (nếu trễ hạn)

    @NotNull
    @Column(name = "remaining_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal remainingBalance = BigDecimal.ZERO; // Dư nợ còn lại sau kỳ này

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "paid_date")
    private LocalDate paidDate; // Ngày thanh toán thực tế

    @Column(name = "paid_amount", precision = 19, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO; // Số tiền đã thanh toán

    @Column(name = "overdue_days")
    private Integer overdueDays = 0; // Số ngày quá hạn

    @Column(name = "is_current_period")
    private Boolean isCurrentPeriod = false; // Đánh dấu kỳ hiện tại cần thanh toán

    @Column(name = "is_overdue")
    private Boolean isOverdue = false; // Đánh dấu kỳ quá hạn

    public enum PaymentStatus {
        PENDING,    // Chưa đến hạn
        DUE,        // Đã đến hạn
        OVERDUE,    // Quá hạn
        PAID,       // Đã thanh toán
        PARTIAL     // Thanh toán một phần
    }

    public MortgagePaymentSchedule() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.principalAmount == null) this.principalAmount = BigDecimal.ZERO;
        if (this.interestAmount == null) this.interestAmount = BigDecimal.ZERO;
        if (this.totalAmount == null) this.totalAmount = BigDecimal.ZERO;
        if (this.penaltyAmount == null) this.penaltyAmount = BigDecimal.ZERO;
        if (this.remainingBalance == null) this.remainingBalance = BigDecimal.ZERO;
        if (this.paidAmount == null) this.paidAmount = BigDecimal.ZERO;
        if (this.overdueDays == null) this.overdueDays = 0;
        if (this.status == null) this.status = PaymentStatus.PENDING;
    }

    // Getters and setters
    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public MortgageAccount getMortgageAccount() {
        return mortgageAccount;
    }

    public void setMortgageAccount(MortgageAccount mortgageAccount) {
        this.mortgageAccount = mortgageAccount;
    }

    public Integer getPeriodNumber() {
        return periodNumber;
    }

    public void setPeriodNumber(Integer periodNumber) {
        this.periodNumber = periodNumber;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public BigDecimal getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Integer getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(Integer overdueDays) {
        this.overdueDays = overdueDays;
    }

    public Boolean getIsCurrentPeriod() {
        return isCurrentPeriod;
    }

    public void setIsCurrentPeriod(Boolean isCurrentPeriod) {
        this.isCurrentPeriod = isCurrentPeriod;
    }

    public Boolean getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(Boolean isOverdue) {
        this.isOverdue = isOverdue;
    }
}
