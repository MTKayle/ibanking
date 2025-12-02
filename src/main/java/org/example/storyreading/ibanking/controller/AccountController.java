package org.example.storyreading.ibanking.controller;

import org.example.storyreading.ibanking.dto.CheckingAccountInfoResponse;
import org.example.storyreading.ibanking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * Lấy thông tin tài khoản checking theo accountNumber
     * Chỉ OFFICER/ADMIN hoặc chính user chủ sở hữu tài khoản mới được truy xuất
     */
    @GetMapping("/{userId}/checking")
    @PreAuthorize("hasRole('OFFICER') or (hasRole('CUSTOMER') and #userId == authentication.principal.userId)")
    public ResponseEntity<CheckingAccountInfoResponse> getCheckingAccountInfo(@PathVariable("userId") Long userId) {
        CheckingAccountInfoResponse resp = accountService.getCheckingAccountInfo(userId);
        return ResponseEntity.ok(resp);
    }

}
