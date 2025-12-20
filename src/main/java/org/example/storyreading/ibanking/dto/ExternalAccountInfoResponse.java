package org.example.storyreading.ibanking.dto;

/**
 * DTO để trả về thông tin tài khoản ngân hàng ngoài
 */
public class ExternalAccountInfoResponse {

    private Long externalAccountId;
    private String fullName;
    private String accountNumber;
    private Long bankId;
    private String bankName;
    private String bankCode;
    private String bankBin;

    // Constructors
    public ExternalAccountInfoResponse() {
    }

    public ExternalAccountInfoResponse(Long externalAccountId, String fullName, String accountNumber,
                                      Long bankId, String bankName, String bankCode, String bankBin) {
        this.externalAccountId = externalAccountId;
        this.fullName = fullName;
        this.accountNumber = accountNumber;
        this.bankId = bankId;
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.bankBin = bankBin;
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

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankBin() {
        return bankBin;
    }

    public void setBankBin(String bankBin) {
        this.bankBin = bankBin;
    }
}

