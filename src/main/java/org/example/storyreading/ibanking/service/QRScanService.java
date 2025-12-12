package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.QRScanRequest;
import org.example.storyreading.ibanking.dto.QRScanResponse;

public interface QRScanService {

    /**
     * Quét và xử lý QR code VietQR
     * Validate account, check bank info và trả về thông tin để chuyển tiền
     *
     * @param request QRScanRequest chứa nội dung QR code
     * @return QRScanResponse chứa thông tin tài khoản, ngân hàng, số tiền
     */
    QRScanResponse scanQRCode(QRScanRequest request);
}

