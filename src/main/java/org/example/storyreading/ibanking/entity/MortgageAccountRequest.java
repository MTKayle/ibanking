// ...new file...
package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "mortgage_account_requests")
public class MortgageAccountRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "id_card_number", length = 20)
    private String idCardNumber;

    @Lob
    @Column(name = "household_registration")
    private String householdRegistration;

    @Column(name = "marriage_certificate", length = 255)
    private String marriageCertificate;

    @Column(name = "labor_contract", length = 255)
    private String laborContract;

    @Column(name = "salary_slip", length = 255)
    private String salarySlip;

    @Column(name = "requested_loan_amount", precision = 15, scale = 2)
    private BigDecimal requestedLoanAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "request_date", nullable = false, updatable = false)
    private Instant requestDate;

    @Column(name = "approval_date")
    private Instant approvalDate;

    @Lob
    @Column(name = "notes")
    private String notes;

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public MortgageAccountRequest() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.requestDate == null) this.requestDate = Instant.now();
        if (this.status == null) this.status = RequestStatus.PENDING;
    }

    // Getters and setters
    public Long getRequestId() {
        return requestId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getHouseholdRegistration() {
        return householdRegistration;
    }

    public void setHouseholdRegistration(String householdRegistration) {
        this.householdRegistration = householdRegistration;
    }

    public String getMarriageCertificate() {
        return marriageCertificate;
    }

    public void setMarriageCertificate(String marriageCertificate) {
        this.marriageCertificate = marriageCertificate;
    }

    public String getLaborContract() {
        return laborContract;
    }

    public void setLaborContract(String laborContract) {
        this.laborContract = laborContract;
    }

    public String getSalarySlip() {
        return salarySlip;
    }

    public void setSalarySlip(String salarySlip) {
        this.salarySlip = salarySlip;
    }

    public BigDecimal getRequestedLoanAmount() {
        return requestedLoanAmount;
    }

    public void setRequestedLoanAmount(BigDecimal requestedLoanAmount) {
        this.requestedLoanAmount = requestedLoanAmount;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Instant getRequestDate() {
        return requestDate;
    }

    public Instant getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Instant approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

