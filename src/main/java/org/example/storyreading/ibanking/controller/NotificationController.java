package org.example.storyreading.ibanking.controller;

import jakarta.validation.Valid;
import org.example.storyreading.ibanking.dto.FcmTokenRequest;
import org.example.storyreading.ibanking.security.CustomUserDetails;
import org.example.storyreading.ibanking.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Đăng ký/Cập nhật FCM token cho user hiện tại
     * POST /api/notifications/register-token
     *
     * Request body:
     * {
     *   "fcmToken": "your_fcm_token_here"
     * }
     */
    @PostMapping("/register-token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> registerFcmToken(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody FcmTokenRequest request) {

        try {
            notificationService.registerFcmToken(userDetails.getUserId(), request.getFcmToken());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đăng ký FCM token thành công");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Xóa FCM token khi user đăng xuất
     * DELETE /api/notifications/remove-token
     */
    @DeleteMapping("/remove-token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> removeFcmToken(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            notificationService.removeFcmToken(userDetails.getUserId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa FCM token thành công");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}

