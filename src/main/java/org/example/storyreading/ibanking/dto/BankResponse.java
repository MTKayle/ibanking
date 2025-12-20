package org.example.storyreading.ibanking.dto;

/**
 * DTO để trả về thông tin ngân hàng
 */
public class BankResponse {

    private Long bankId;
    private String bankBin;
    private String bankCode;
    private String bankName;
    private String logoUrl;

    // Constructors
    public BankResponse() {
    }

    public BankResponse(Long bankId, String bankBin, String bankCode, String bankName, String logoUrl) {
        this.bankId = bankId;
        this.bankBin = bankBin;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.logoUrl = logoUrl;
    }

    // Getters and Setters
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

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}

