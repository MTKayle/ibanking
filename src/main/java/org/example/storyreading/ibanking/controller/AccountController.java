package org.example.storyreading.ibanking.controller;

import org.example.storyreading.ibanking.dto.AccountInfoResponse;
import org.example.storyreading.ibanking.dto.CheckingAccountInfoResponse;
import org.example.storyreading.ibanking.dto.QRCodeRequest;
import org.example.storyreading.ibanking.dto.QRCodeResponse;
import org.example.storyreading.ibanking.service.AccountService;
import org.example.storyreading.ibanking.service.QRCodeService;
import org.example.storyreading.ibanking.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Base64;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private QRCodeService qrCodeService;

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

    /**
     * Lấy thông tin tài khoản theo account number
     * Trả về thông tin account holder, bank và account type
     *
     * @param accountNumber Số tài khoản
     * @return AccountInfoResponse với đầy đủ thông tin
     */
    @GetMapping("/info/{accountNumber}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<AccountInfoResponse> getAccountInfoByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
        AccountInfoResponse response = accountService.getAccountInfoByAccountNumber(accountNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Tạo mã QR VietQR cho tài khoản checking của user
     * Trả về ảnh PNG trực tiếp
     *
     * @param request (optional) QRCodeRequest chứa amount và description
     * @return PNG image của mã QR
     */
    @PostMapping(value = "/checking/qr-code", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<byte[]> generateCheckingAccountQRCode(@Valid @RequestBody(required = false) QRCodeRequest request) {
        // Lấy user ID từ authentication context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        // Nếu không có request body, tạo request rỗng (static QR)
        if (request == null) {
            request = new QRCodeRequest();
        }

        QRCodeResponse response = qrCodeService.generateQRCodeForCheckingAccount(userId, request);

        // Decode Base64 string thành byte array
        byte[] imageBytes = Base64.getDecoder().decode(response.getQrCodeBase64());

        // Trả về ảnh PNG
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(imageBytes.length);
        headers.set("X-Account-Number", response.getAccountNumber());
        headers.set("X-Account-Holder", response.getAccountHolderName());
        if (response.getAmount() != null) {
            headers.set("X-Amount", response.getAmount().toString());
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(imageBytes);
    }
}
