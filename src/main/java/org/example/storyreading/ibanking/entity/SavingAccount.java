package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "saving_accounts")
public class SavingAccount {

    @Id
    @Column(name = "saving_id", length = 50)
    private Long savingId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "saving_id", referencedColumnName = "account_id")
    private Account account;

    @NotNull
    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private AccountType type = AccountType.DEMAND;

    // interest rate updated by banking officers
    @Column(name = "interest_rate", precision = 10, scale = 4)
    private BigDecimal interestRate = BigDecimal.ZERO;

    @Column(name = "opened_date")
    private LocalDate openedDate;

    @Column(name = "maturity_date")
    private LocalDate maturityDate; // null if non-term

    // profit per month (calculated or stored)
    @Column(name = "profit_per_month", precision = 19, scale = 2)
    private BigDecimal profitPerMonth = BigDecimal.ZERO;

    @Column(name = "next_profit_date")
    private LocalDate nextProfitDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    public enum AccountType {
        FIXED_TERM,
        DEMAND
    }

    public enum Status {
        ACTIVE,
        CLOSED,
        LOCKED
    }

    public SavingAccount() {
    }

    public SavingAccount(Account account,
                         BigDecimal balance,
                         AccountType type,
                         BigDecimal interestRate,
                         LocalDate openedDate,
                         LocalDate maturityDate,
                         BigDecimal profitPerMonth,
                         LocalDate nextProfitDate,
                         Status status) {
        this.account = account;
        this.savingId = (account != null) ? account.getAccountId() : null;
        this.balance = (balance != null) ? balance : BigDecimal.ZERO;
        this.type = (type != null) ? type : AccountType.DEMAND;
        this.interestRate = (interestRate != null) ? interestRate : BigDecimal.ZERO;
        this.openedDate = openedDate;
        this.maturityDate = maturityDate;
        this.profitPerMonth = (profitPerMonth != null) ? profitPerMonth : BigDecimal.ZERO;
        this.nextProfitDate = nextProfitDate;
        this.status = (status != null) ? status : Status.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        if (this.balance == null) this.balance = BigDecimal.ZERO;
        if (this.interestRate == null) this.interestRate = BigDecimal.ZERO;
        if (this.profitPerMonth == null) this.profitPerMonth = BigDecimal.ZERO;
        if (this.status == null) this.status = Status.ACTIVE;
        if (this.account != null && this.account.getAccountNumber() != null) {
            this.savingId = this.account.getAccountId();
        }
    }

    // Getters and setters
    public Long getSavingId() {
        return savingId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
        if (account != null) this.savingId = account.getAccountId();
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public LocalDate getOpenedDate() {
        return openedDate;
    }

    public void setOpenedDate(LocalDate openedDate) {
        this.openedDate = openedDate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }

    public BigDecimal getProfitPerMonth() {
        return profitPerMonth;
    }

    public void setProfitPerMonth(BigDecimal profitPerMonth) {
        this.profitPerMonth = profitPerMonth;
    }

    public LocalDate getNextProfitDate() {
        return nextProfitDate;
    }

    public void setNextProfitDate(LocalDate nextProfitDate) {
        this.nextProfitDate = nextProfitDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
