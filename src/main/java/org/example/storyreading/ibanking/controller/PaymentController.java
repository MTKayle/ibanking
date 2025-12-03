package org.example.storyreading.ibanking.controller;

import jakarta.validation.Valid;
import org.example.storyreading.ibanking.dto.DepositRequest;
import org.example.storyreading.ibanking.dto.DepositResponse;
import org.example.storyreading.ibanking.dto.TransferRequest;
import org.example.storyreading.ibanking.dto.TransferResponse;
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
}

