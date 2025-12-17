// ...new file...
package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "accounts",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "account_number")
       }
)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.active;

    @NotNull
    @Column(name = "account_number", nullable = false, unique = true, length = 50)
    private String accountNumber;

    public enum AccountType {
        checking,
        saving,
        mortgage
    }

    public enum Status {
        active,
        closed
    }

    public Account() {
        // default constructor
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        if (this.status == null) {
            this.status = Status.active;
        }
    }

    // Getters and setters

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    // Convenience builder-like setters
    public Account withUser(User user) {
        setUser(user);
        return this;
    }

    public Account withAccountType(AccountType type) {
        setAccountType(type);
        return this;
    }

    public Account withAccountNumber(String number) {
        setAccountNumber(number);
        return this;
    }

    public Account withStatus(Status status) {
        setStatus(status);
        return this;
    }
}

