package org.example.storyreading.ibanking.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.storyreading.ibanking.dto.VNPayDepositRequest;
import org.example.storyreading.ibanking.dto.VNPayDepositResponse;
import org.example.storyreading.ibanking.dto.VNPayCallbackResponse;
import org.example.storyreading.ibanking.security.CustomUserDetails;
import org.example.storyreading.ibanking.service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;

    /**
     * Tạo URL thanh toán VNPay để nạp tiền vào tài khoản
     * POST /api/vnpay/create-payment
     */
    @PostMapping(value = "/create-payment", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<VNPayDepositResponse> createPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody VNPayDepositRequest request,
            HttpServletRequest httpRequest) {

        try {
            VNPayDepositResponse response = vnPayService.createPaymentUrl(
                    userDetails.getUserId(), request, httpRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(VNPayDepositResponse.error(e.getMessage()));
        }
    }

    /**
     * Callback URL từ VNPay sau khi thanh toán
     * GET /api/vnpay/callback
     */
    @GetMapping("/callback")
    public ResponseEntity<VNPayCallbackResponse> vnpayCallback(@RequestParam Map<String, String> params) {
        VNPayCallbackResponse response = vnPayService.processCallback(params);
        return ResponseEntity.ok(response);
    }
}
