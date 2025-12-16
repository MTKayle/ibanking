package org.example.storyreading.ibanking.controller;

import jakarta.validation.Valid;
import org.example.storyreading.ibanking.dto.DepositRequest;
import org.example.storyreading.ibanking.dto.DepositResponse;
import org.example.storyreading.ibanking.dto.TransferRequest;
import org.example.storyreading.ibanking.dto.TransferResponse;
import org.example.storyreading.ibanking.dto.OtpResponse;
import org.example.storyreading.ibanking.dto.VerifyOtpRequest;
import org.example.storyreading.ibanking.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Nạp tiền vào tài khoản checking (dành cho OFFICER)
     */
    @PostMapping("/checking/deposit")
    @PreAuthorize("hasRole('OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<DepositResponse> depositToChecking(@Valid @RequestBody DepositRequest depositRequest) {
        DepositResponse response = paymentService.depositToCheckingAccount(depositRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Chuyển tiền giữa 2 tài khoản checking
     * Sử dụng pessimistic lock (SELECT FOR UPDATE) để tránh race condition
     * Lock theo thứ tự accountId để tránh deadlock
     * Chỉ cho phép chuyển tiền giữa các tài khoản checking đang active
     */
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<TransferResponse> transferMoney(@Valid @RequestBody TransferRequest transferRequest) {
        TransferResponse response = paymentService.transferMoney(transferRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Khởi tạo giao dịch chuyển tiền với OTP
     * - Validate các ràng buộc
     * - Tạo transaction với trạng thái PENDING
     * - Tạo OTP 6 số với thời gian hết hạn 1 phút
     * - Trả về OTP và transaction code cho client
     */
    @PostMapping("/transfer/initiate")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<OtpResponse> initiateTransferWithOtp(@Valid @RequestBody TransferRequest transferRequest) {
        OtpResponse response = paymentService.initiateTransferWithOtp(transferRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Xác nhận giao dịch chuyển tiền bằng OTP
     * - Verify OTP
     * - Thực hiện chuyển tiền
     * - Update transaction status thành SUCCESS hoặc FAILED
     */
    @PostMapping("/transfer/confirm")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<TransferResponse> confirmTransferWithOtp(@Valid @RequestBody VerifyOtpRequest verifyRequest) {
        TransferResponse response = paymentService.confirmTransferWithOtp(verifyRequest.getTransactionCode());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
