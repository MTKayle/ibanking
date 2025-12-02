// ...new file...
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

    @Column(name = "monthly_principal_payment_amount", precision = 19, scale = 2)
    private BigDecimal monthlyPrincipalPaymentAmount = BigDecimal.ZERO;

    @Column(name = "term_months")
    private Integer termMonths;

    @Column(name = "start_date")
    private LocalDate startDate;

    public MortgageAccount() {
    }

    public MortgageAccount(Account account,
                           BigDecimal principalAmount,
                           BigDecimal interestRate,
                           BigDecimal monthlyPrincipalPaymentAmount,
                           Integer termMonths,
                           LocalDate startDate) {
        this.account = account;
        this.mortgageId = (account != null) ? account.getAccountId() : null;
        this.principalAmount = (principalAmount != null) ? principalAmount : BigDecimal.ZERO;
        this.interestRate = (interestRate != null) ? interestRate : BigDecimal.ZERO;
        this.monthlyPrincipalPaymentAmount = (monthlyPrincipalPaymentAmount != null) ? monthlyPrincipalPaymentAmount : BigDecimal.ZERO;
        this.termMonths = termMonths;
        this.startDate = startDate;
    }

    @PrePersist
    protected void onCreate() {
        if (this.principalAmount == null) this.principalAmount = BigDecimal.ZERO;
        if (this.interestRate == null) this.interestRate = BigDecimal.ZERO;
        if (this.monthlyPrincipalPaymentAmount == null) this.monthlyPrincipalPaymentAmount = BigDecimal.ZERO;
        if (this.account != null && this.account.getAccountId() != null) {
            this.mortgageId = this.account.getAccountId();
        }
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
        if (account != null) this.mortgageId = account.getAccountId();
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

    public BigDecimal getMonthlyPrincipalPaymentAmount() {
        return monthlyPrincipalPaymentAmount;
    }

    public void setMonthlyPrincipalPaymentAmount(BigDecimal monthlyPrincipalPaymentAmount) {
        this.monthlyPrincipalPaymentAmount = monthlyPrincipalPaymentAmount;
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
}

