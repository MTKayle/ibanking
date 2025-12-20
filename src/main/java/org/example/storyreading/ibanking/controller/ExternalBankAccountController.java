package org.example.storyreading.ibanking.controller;

import org.example.storyreading.ibanking.dto.ExternalAccountInfoResponse;
import org.example.storyreading.ibanking.service.ExternalBankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/external-accounts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ExternalBankAccountController {

    @Autowired
    private ExternalBankAccountService externalBankAccountService;

    /**
     * Lấy thông tin tài khoản ngân hàng ngoài theo bankBin và accountNumber
     * GET /api/external-accounts/info?bankBin=xxx&accountNumber=xxx
     */
    @GetMapping("/info")
    public ResponseEntity<?> getAccountInfo(
            @RequestParam("bankBin") String bankBin,
            @RequestParam("accountNumber") String accountNumber) {
        try {
            ExternalAccountInfoResponse accountInfo = externalBankAccountService
                    .getAccountInfoByBankBinAndAccountNumber(bankBin, accountNumber);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy thông tin tài khoản thành công");
            response.put("data", accountInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}

