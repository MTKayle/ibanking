package org.example.storyreading.ibanking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

/**
 * Entity lưu thông tin tài khoản ngân hàng ngoài
 * Lưu thông tin user riêng biệt, không liên kết với bảng users
 * Chỉ có foreign key tới bảng banks để biết tài khoản thuộc ngân hàng nào
 */
@Entity
@Table(name = "external_bank_accounts",
       indexes = {
           @Index(name = "idx_bank_id", columnList = "bank_id"),
           @Index(name = "idx_account_number", columnList = "account_number")
       }
)
public class ExternalBankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "external_account_id")
    private Long externalAccountId;

    // Thông tin user - lưu riêng biệt, không foreign key
    @NotBlank
    @Size(max = 100)
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    // Số tài khoản ngân hàng
    @NotBlank
    @Size(max = 50)
    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    // Quan hệ với Bank - tài khoản này thuộc ngân hàng nào
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false, referencedColumnName = "bank_id")
    private Bank bank;




    // Constructors
    public ExternalBankAccount() {
    }

    public ExternalBankAccount(String fullName, String accountNumber, Bank bank) {
        this.fullName = fullName;
        this.accountNumber = accountNumber;
        this.bank = bank;
    }

    // Getters and Setters
    public Long getExternalAccountId() {
        return externalAccountId;
    }

    public void setExternalAccountId(Long externalAccountId) {
        this.externalAccountId = externalAccountId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }





    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }


    @Override
    public String toString() {
        return "ExternalBankAccount{" +
                "externalAccountId=" + externalAccountId +
                ", fullName='" + fullName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
