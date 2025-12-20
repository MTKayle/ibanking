package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

@Entity
@Table(name = "banks",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "bank_bin"),
           @UniqueConstraint(columnNames = "bank_code")
       }
)
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_id")
    private Long bankId;

    @NotBlank
    @Size(max = 10)
    @Column(name = "bank_bin", nullable = false, unique = true, length = 10)
    private String bankBin;

    @NotBlank
    @Size(max = 20)
    @Column(name = "bank_code", nullable = false, unique = true, length = 20)
    private String bankCode;

    @NotBlank
    @Size(max = 100)
    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Bank() {
    }

    public Bank(String bankBin, String bankCode, String bankName) {
        this.bankBin = bankBin;
        this.bankCode = bankCode;
        this.bankName = bankName;
    }

    public Bank(String bankBin, String bankCode, String bankName, String logoUrl) {
        this.bankBin = bankBin;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.logoUrl = logoUrl;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // Getters and setters
    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getBankBin() {
        return bankBin;
    }

    public void setBankBin(String bankBin) {
        this.bankBin = bankBin;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
