// ...new file...
package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mortgage_payment_schedules")
public class MortgagePaymentSchedule {

    @EmbeddedId
    private MortgagePaymentScheduleId id;

    @MapsId("mortgageId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mortgage_id", referencedColumnName = "mortgage_id")
    private MortgageAccount mortgage;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @NotNull
    @Column(name = "principal_due", nullable = false, precision = 19, scale = 2)
    private BigDecimal principalDue = BigDecimal.ZERO;

    @Column(name = "interest_due", precision = 19, scale = 2)
    private BigDecimal interestDue = BigDecimal.ZERO;

    @Column(name = "late_fee", precision = 19, scale = 2)
    private BigDecimal lateFee = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING,
        PAID,
        LATE
    }

    public MortgagePaymentSchedule() {
    }

    public MortgagePaymentSchedule(MortgagePaymentScheduleId id, MortgageAccount mortgage) {
        this.id = id;
        this.mortgage = mortgage;
    }

    // Getters and setters
    public MortgagePaymentScheduleId getId() {
        return id;
    }

    public void setId(MortgagePaymentScheduleId id) {
        this.id = id;
    }

    public MortgageAccount getMortgage() {
        return mortgage;
    }

    public void setMortgage(MortgageAccount mortgage) {
        this.mortgage = mortgage;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getPrincipalDue() {
        return principalDue;
    }

    public void setPrincipalDue(BigDecimal principalDue) {
        this.principalDue = principalDue;
    }

    public BigDecimal getInterestDue() {
        return interestDue;
    }

    public void setInterestDue(BigDecimal interestDue) {
        this.interestDue = interestDue;
    }

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

