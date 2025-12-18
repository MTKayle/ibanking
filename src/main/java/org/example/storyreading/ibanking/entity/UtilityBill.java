package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "utility_bills")
public class UtilityBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_id")
    private Long billId;

    @Column(name = "bill_code", unique = true, nullable = false, length = 20)
    private String billCode; // Mã hóa đơn (VD: EVN202412001, VNW202412001)

    @Enumerated(EnumType.STRING)
    @Column(name = "bill_type", nullable = false, length = 20)
    private UtilityBillType billType; // ELECTRICITY, WATER, INTERNET, PHONE

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "customer_address", nullable = false, length = 255)
    private String customerAddress;

    @Column(name = "customer_phone", length = 15)
    private String customerPhone;

    @Column(name = "period", nullable = false, length = 7)
    private String period; // Kỳ hóa đơn (VD: "2024-12")

    @Column(name = "usage_amount")
    private Integer usageAmount; // Số điện/nước sử dụng (kWh hoặc m3)

    @Column(name = "old_index")
    private Integer oldIndex; // Chỉ số cũ

    @Column(name = "new_index")
    private Integer newIndex; // Chỉ số mới

    @Column(name = "unit_price", precision = 19, scale = 2)
    private BigDecimal unitPrice; // Đơn giá

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount; // Số tiền phải trả

    @Column(name = "vat", precision = 19, scale = 2)
    private BigDecimal vat; // Thuế VAT

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount; // Tổng tiền (amount + vat)

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate; // Ngày phát hành hóa đơn

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate; // Ngày hết hạn thanh toán

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UtilityBillStatus status; // UNPAID, PAID, OVERDUE, CANCELLED

    @Column(name = "payment_time")
    private LocalDateTime paymentTime; // Thời gian thanh toán

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_user_id", referencedColumnName = "user_id", nullable = true)
    private User paidByUser; // User đã thanh toán

    @Column(name = "transaction_id")
    private Long transactionId; // ID giao dịch thanh toán

    @Column(name = "provider_name", length = 100)
    private String providerName; // Tên nhà cung cấp (VD: "EVN HCMC", "SAWACO")

    @Column(name = "provider_code", length = 20)
    private String providerCode; // Mã nhà cung cấp

    @Column(name = "notes", length = 500)
    private String notes; // Ghi chú

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public UtilityBill() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = UtilityBillStatus.UNPAID;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public UtilityBillType getBillType() {
        return billType;
    }

    public void setBillType(UtilityBillType billType) {
        this.billType = billType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Integer getUsageAmount() {
        return usageAmount;
    }

    public void setUsageAmount(Integer usageAmount) {
        this.usageAmount = usageAmount;
    }

    public Integer getOldIndex() {
        return oldIndex;
    }

    public void setOldIndex(Integer oldIndex) {
        this.oldIndex = oldIndex;
    }

    public Integer getNewIndex() {
        return newIndex;
    }

    public void setNewIndex(Integer newIndex) {
        this.newIndex = newIndex;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public UtilityBillStatus getStatus() {
        return status;
    }

    public void setStatus(UtilityBillStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public User getPaidByUser() {
        return paidByUser;
    }

    public void setPaidByUser(User paidByUser) {
        this.paidByUser = paidByUser;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
