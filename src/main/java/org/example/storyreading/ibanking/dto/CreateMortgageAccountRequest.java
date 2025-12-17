package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.storyreading.ibanking.entity.MortgageAccount;
import org.example.storyreading.ibanking.entity.CollateralType;

public class CreateMortgageAccountRequest {

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    @NotNull(message = "Loại tài sản thế chấp không được để trống")
    private CollateralType collateralType;

    private String collateralDescription;

    @NotNull(message = "Loại kỳ thanh toán không được để trống")
    private MortgageAccount.PaymentFrequency paymentFrequency;

    // Getters and setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public CollateralType getCollateralType() {
        return collateralType;
    }

    public void setCollateralType(CollateralType collateralType) {
        this.collateralType = collateralType;
    }

    public String getCollateralDescription() {
        return collateralDescription;
    }

    public void setCollateralDescription(String collateralDescription) {
        this.collateralDescription = collateralDescription;
    }

    public MortgageAccount.PaymentFrequency getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setPaymentFrequency(MortgageAccount.PaymentFrequency paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }
}
