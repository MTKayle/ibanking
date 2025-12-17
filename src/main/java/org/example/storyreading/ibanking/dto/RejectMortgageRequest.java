package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RejectMortgageRequest {

    @NotNull(message = "ID tài khoản vay không được để trống")
    private Long mortgageId;

    @NotBlank(message = "Lý do từ chối không được để trống")
    private String rejectionReason;

    // Getters and setters
    public Long getMortgageId() {
        return mortgageId;
    }

    public void setMortgageId(Long mortgageId) {
        this.mortgageId = mortgageId;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}

