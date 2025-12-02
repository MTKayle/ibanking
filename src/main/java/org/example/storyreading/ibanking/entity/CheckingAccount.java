// ...new file...
package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "checking_accounts")
public class CheckingAccount {

    @Id
    @Column(name = "checking_id")
    private Long checkingId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "checking_id", referencedColumnName = "account_id")
    private Account account;

    @NotNull
    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "overdraft_limit", precision = 19, scale = 2)
    private BigDecimal overdraftLimit = BigDecimal.ZERO;

    public CheckingAccount() {
    }

    public CheckingAccount(Account account, BigDecimal balance, BigDecimal overdraftLimit) {
        this.account = account;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.overdraftLimit = overdraftLimit != null ? overdraftLimit : BigDecimal.ZERO;
    }

    @PrePersist
    protected void onCreate() {
        if (this.balance == null) this.balance = BigDecimal.ZERO;
        if (this.overdraftLimit == null) this.overdraftLimit = BigDecimal.ZERO;
    }

    // Getters and setters
    public Long getCheckingId() {
        return checkingId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(BigDecimal overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }
}

