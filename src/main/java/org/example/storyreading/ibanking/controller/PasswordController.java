package org.example.storyreading.ibanking.controller;

import jakarta.validation.Valid;
import org.example.storyreading.ibanking.dto.ChangePasswordRequest;
import org.example.storyreading.ibanking.dto.ChangePasswordResponse;
import org.example.storyreading.ibanking.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/password")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    /**
     * API đổi mật khẩu
     * FE gửi phone và mật khẩu mới
     * BE tìm user theo phone, hash mật khẩu mới và update vào database
     *
     * @param request chứa phone và newPassword
     * @return ChangePasswordResponse
     */
    @PostMapping("/change")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            ChangePasswordResponse response = passwordService.changePassword(request);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi đổi mật khẩu: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}

