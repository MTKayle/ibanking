package org.example.storyreading.ibanking.controller;

import jakarta.validation.Valid;
import org.example.storyreading.ibanking.dto.QRScanRequest;
import org.example.storyreading.ibanking.dto.QRScanResponse;
import org.example.storyreading.ibanking.service.QRScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qr")
@CrossOrigin(origins = "*", maxAge = 3600)
public class QRController {

    @Autowired
    private QRScanService qrScanService;

    /**
     * API để quét và xử lý QR code VietQR
     * Trả về thông tin tài khoản, ngân hàng, số tiền để thực hiện chuyển tiền
     *
     * @param request QRScanRequest chứa nội dung QR đã quét
     * @return QRScanResponse với thông tin chi tiết tài khoản và ngân hàng
     */
    @PostMapping("/scan")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<QRScanResponse> scanQRCode(@Valid @RequestBody QRScanRequest request) {
        QRScanResponse response = qrScanService.scanQRCode(request);
        return ResponseEntity.ok(response);
    }
}

