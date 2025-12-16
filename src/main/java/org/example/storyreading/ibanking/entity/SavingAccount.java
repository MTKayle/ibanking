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

    @Column(name = "saving_book_number", unique = true, length = 50)
    private String savingBookNumber; // STK-20251216001

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "term", nullable = false, length = 30)
    private SavingTerm term = SavingTerm.NON_TERM;

    // interest rate updated by banking officers
    @Column(name = "interest_rate", precision = 10, scale = 4)
    private BigDecimal interestRate = BigDecimal.ZERO;

    @Column(name = "opened_date")
    private LocalDate openedDate;

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    public enum Status {
        ACTIVE,      // Đang hoạt động
        MATURED,     // Đã đáo hạn
        CLOSED       // Đã tất toán
    }

    public SavingAccount() {
    }

    public SavingAccount(Account account,
                         BigDecimal balance,
                         String savingBookNumber,
                         SavingTerm term,
                         BigDecimal interestRate,
                         LocalDate openedDate,
                         LocalDate maturityDate,
                         Status status) {
        this.account = account;
        this.balance = (balance != null) ? balance : BigDecimal.ZERO;
        this.savingBookNumber = savingBookNumber;
        this.term = (term != null) ? term : SavingTerm.NON_TERM;
        this.interestRate = (interestRate != null) ? interestRate : BigDecimal.ZERO;
        this.openedDate = openedDate;
        this.maturityDate = maturityDate;
        this.status = (status != null) ? status : Status.ACTIVE;
    }

    // Getters and setters
    public Long getSavingId() {
        return savingId;
    }

    public void setSavingId(Long savingId) {
        this.savingId = savingId;
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

    public String getSavingBookNumber() {
        return savingBookNumber;
    }

    public void setSavingBookNumber(String savingBookNumber) {
        this.savingBookNumber = savingBookNumber;
    }

    public SavingTerm getTerm() {
        return term;
    }

    public void setTerm(SavingTerm term) {
        this.term = term;
        // Don't auto-set interest rate here - it should be set explicitly from the database
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        if (this.balance == null) this.balance = BigDecimal.ZERO;
        if (this.interestRate == null) this.interestRate = BigDecimal.ZERO;
        if (this.status == null) this.status = Status.ACTIVE;
    }
}
