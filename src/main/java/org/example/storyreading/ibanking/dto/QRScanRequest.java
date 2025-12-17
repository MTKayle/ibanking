package org.example.storyreading.ibanking.dto;

import jakarta.validation.constraints.NotBlank;

public class QRScanRequest {

    @NotBlank(message = "Nội dung QR không được để trống")
    private String qrContent;

    public QRScanRequest() {}

    public QRScanRequest(String qrContent) {
        this.qrContent = qrContent;
    }

    public String getQrContent() {
        return qrContent;
    }

    public void setQrContent(String qrContent) {
        this.qrContent = qrContent;
    }
}

