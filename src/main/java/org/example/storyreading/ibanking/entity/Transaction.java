package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions",
       uniqueConstraints = @UniqueConstraint(columnNames = "code")
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    // Account gửi tiền (sender) - nullable cho trường hợp DEPOSIT từ bên ngoài
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "sender_account_id", referencedColumnName = "account_id")
    private Account senderAccount;

    // Account nhận tiền (receiver) - nullable cho trường hợp WITHDRAW ra bên ngoài
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "receiver_account_id", referencedColumnName = "account_id")
    private Account receiverAccount;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 50)
    private TransactionType transactionType;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    public enum TransactionType {
        DEPOSIT,        // Nạp tiền: chỉ có receiverAccount
        WITHDRAW,       // Rút tiền: chỉ có senderAccount
        TRANSFER,       // Chuyển khoản: có cả senderAccount và receiverAccount
        LOAN_PAYMENT,   // Trả nợ: senderAccount trả cho mortgage account
        INTEREST_INCOME // Lãi: receiverAccount nhận lãi
    }

    public Transaction() {
    }

    public Transaction(Account senderAccount, Account receiverAccount, BigDecimal amount,
                      TransactionType transactionType, String description, String code) {
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.code = code;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = Instant.now();
    }

    // Getters and setters
    public Long getTransactionId() {
        return transactionId;
    }

    public Account getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(Account senderAccount) {
        this.senderAccount = senderAccount;
    }

    public Account getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(Account receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
