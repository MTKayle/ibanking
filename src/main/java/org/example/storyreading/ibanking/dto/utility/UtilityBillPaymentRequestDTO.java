package org.example.storyreading.ibanking.dto.utility;

import jakarta.validation.constraints.NotBlank;

public class UtilityBillPaymentRequestDTO {

    @NotBlank(message = "Mã hóa đơn không được để trống")
    private String billCode;

    // Getters and Setters
    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }
}

