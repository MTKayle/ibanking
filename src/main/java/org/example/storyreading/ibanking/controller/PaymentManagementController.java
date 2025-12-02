package org.example.storyreading.ibanking.controller;

import jakarta.validation.Valid;
import org.example.storyreading.ibanking.dto.DepositRequest;
import org.example.storyreading.ibanking.dto.DepositResponse;
import org.example.storyreading.ibanking.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentManagementController {

    @Autowired
    private PaymentService paymentService;

    /**
     * API nạp tiền vào tài khoản checking
     * Chỉ officer hoặc admin mới có quyền sử dụng
     * Sử dụng pessimistic lock để đảm bảo tính toàn vẹn dữ liệu
     * Tự động ghi giao dịch vào bảng transactions
     *
     * @param depositRequest chứa accountNumber, amount, và description
     * @return DepositResponse với thông tin số dư mới và mã giao dịch
     */
    @PostMapping("/checking/deposit")
    @PreAuthorize("hasRole('OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<DepositResponse> depositToChecking(@Valid @RequestBody DepositRequest depositRequest) {
        DepositResponse response = paymentService.depositToCheckingAccount(depositRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
