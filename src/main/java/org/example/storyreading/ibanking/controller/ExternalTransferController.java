package org.example.storyreading.ibanking.controller;

import jakarta.validation.Valid;
import org.example.storyreading.ibanking.dto.ExternalTransferRequest;
import org.example.storyreading.ibanking.dto.ExternalTransferResponse;
import org.example.storyreading.ibanking.dto.OtpResponse;
import org.example.storyreading.ibanking.service.ExternalTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/external-transfer")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ExternalTransferController {

    @Autowired
    private ExternalTransferService externalTransferService;

    /**
     * Khởi tạo giao dịch chuyển tiền ngoài ngân hàng với OTP
     * POST /api/external-transfer/initiate
     *
     * - Validate các ràng buộc
     * - Tạo ExternalTransfer với trạng thái PENDING
     * - Tạo OTP 6 số với thời gian hết hạn 1 phút
     * - Chưa trừ tiền ở bước này
     * - Trả về OTP và transaction code cho client
     */
    @PostMapping("/initiate")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<?> initiateExternalTransfer(@Valid @RequestBody ExternalTransferRequest request) {
        try {
            OtpResponse response = externalTransferService.initiateExternalTransfer(request);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Khởi tạo giao dịch chuyển tiền ngoài ngân hàng thành công");
            result.put("data", response);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Xác nhận giao dịch chuyển tiền ngoài ngân hàng bằng OTP
     * POST /api/external-transfer/confirm
     *
     * - Verify OTP và transaction code
     * - Trừ tiền từ tài khoản sender (không cộng tiền cho receiver vì ngoài hệ thống)
     * - Update transaction status thành SUCCESS hoặc FAILED
     */
    @PostMapping("/confirm")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<?> confirmExternalTransfer(
            @RequestParam("transactionCode") String transactionCode) {
        try {
            ExternalTransferResponse response = externalTransferService.confirmExternalTransfer(
                    transactionCode);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Xác nhận chuyển tiền ngoài ngân hàng thành công");
            result.put("data", response);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}

