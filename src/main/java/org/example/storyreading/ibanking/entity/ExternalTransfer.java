package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Entity lưu giao dịch chuyển tiền ngoài ngân hàng
 * Chỉ trừ tiền từ tài khoản sender, không cộng tiền cho tài khoản nhận
 */
@Entity
@Table(name = "external_transfers",
       uniqueConstraints = @UniqueConstraint(columnNames = "transaction_code")
)
public class ExternalTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "external_transfer_id")
    private Long externalTransferId;

    // Account gửi tiền (trong hệ thống)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_account_id", nullable = false, referencedColumnName = "account_id")
    private Account senderAccount;

    // Thông tin ngân hàng nhận
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_bank_id", nullable = false, referencedColumnName = "bank_id")
    private Bank receiverBank;

    // Số tài khoản nhận (ngoài hệ thống)
    @NotBlank
    @Column(name = "receiver_account_number", nullable = false, length = 50)
    private String receiverAccountNumber;

    // Tên người nhận
    @NotBlank
    @Column(name = "receiver_name", nullable = false, length = 100)
    private String receiverName;

    // Số tiền chuyển
    @NotNull
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    // Mô tả giao dịch
    @Column(name = "description", length = 255)
    private String description;

    // Mã giao dịch (unique)
    @NotBlank
    @Column(name = "transaction_code", nullable = false, unique = true, length = 100)
    private String transactionCode;

    // Trạng thái giao dịch
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransferStatus status = TransferStatus.PENDING;

    // OTP code (6 digits)
    @Column(name = "otp_code", length = 10)
    private String otpCode;

    // Thời gian hết hạn OTP
    @Column(name = "otp_expiry")
    private Instant otpExpiry;

    // Thời gian tạo giao dịch
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Thời gian hoàn thành giao dịch
    @Column(name = "completed_at")
    private Instant completedAt;

    public enum TransferStatus {
        PENDING,   // Đang chờ xác nhận OTP
        SUCCESS,   // Chuyển tiền thành công
        FAILED,    // Chuyển tiền thất bại
        EXPIRED    // OTP hết hạn
    }

    public ExternalTransfer() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.status == null) {
            this.status = TransferStatus.PENDING;
        }
    }

    // Getters and Setters
    public Long getExternalTransferId() {
        return externalTransferId;
    }

    public void setExternalTransferId(Long externalTransferId) {
        this.externalTransferId = externalTransferId;
    }

    public Account getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(Account senderAccount) {
        this.senderAccount = senderAccount;
    }

    public Bank getReceiverBank() {
        return receiverBank;
    }

    public void setReceiverBank(Bank receiverBank) {
        this.receiverBank = receiverBank;
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

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public Instant getOtpExpiry() {
        return otpExpiry;
    }

    public void setOtpExpiry(Instant otpExpiry) {
        this.otpExpiry = otpExpiry;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}

