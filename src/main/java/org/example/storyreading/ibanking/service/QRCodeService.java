package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.QRCodeRequest;
import org.example.storyreading.ibanking.dto.QRCodeResponse;

public interface QRCodeService {

    /**
     * Generate QR code for checking account
     * @param userId User ID (must be authenticated user)
     * @param request QR code request containing optional amount and description
     * @return QRCodeResponse containing QR code image in Base64 and account details
     */
    QRCodeResponse generateQRCodeForCheckingAccount(Long userId, QRCodeRequest request);
}

