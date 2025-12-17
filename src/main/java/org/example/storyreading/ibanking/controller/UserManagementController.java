package org.example.storyreading.ibanking.controller;

import jakarta.validation.Valid;
import org.example.storyreading.ibanking.dto.LockUserRequest;
import org.example.storyreading.ibanking.dto.UpdateUserRequest;
import org.example.storyreading.ibanking.dto.UserResponse;
import org.example.storyreading.ibanking.dto.SmartFlagsRequest;
import org.example.storyreading.ibanking.dto.UpdateSmartOtpRequest;
import org.example.storyreading.ibanking.dto.FeatureStatusResponse;
import org.example.storyreading.ibanking.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    /**
     * Get all users - Only OFFICER can access
     */
    @GetMapping
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userManagementService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID - Only OFFICER can access
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse user = userManagementService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Check if face recognition is enabled for a user
     */
    @GetMapping("/{userId}/features/face-recognition")
    @PreAuthorize("hasRole('OFFICER') or (hasRole('CUSTOMER') and #userId == authentication.principal.userId)")
    public ResponseEntity<FeatureStatusResponse> isFaceRecognitionEnabled(@PathVariable Long userId) {
        FeatureStatusResponse status = userManagementService.isFaceRecognitionEnabled(userId);
        return ResponseEntity.ok(status);
    }

    /**
     * Check if smart eKYC is enabled for a user
     */
    @GetMapping("/{userId}/features/smart-ekyc")
    @PreAuthorize("hasRole('OFFICER') or (hasRole('CUSTOMER') and #userId == authentication.principal.userId)")
    public ResponseEntity<FeatureStatusResponse> isSmartEkycEnabled(@PathVariable Long userId) {
        FeatureStatusResponse status = userManagementService.isSmartEkycEnabled(userId);
        return ResponseEntity.ok(status);
    }

    /**
     * Check if fingerprint login is enabled for a user
     */
    @GetMapping("/{userId}/features/fingerprint-login")
    @PreAuthorize("hasRole('OFFICER') or (hasRole('CUSTOMER') and #userId == authentication.principal.userId)")
    public ResponseEntity<FeatureStatusResponse> isFingerprintLoginEnabled(@PathVariable Long userId) {
        FeatureStatusResponse status = userManagementService.isFingerprintLoginEnabled(userId);
        return ResponseEntity.ok(status);
    }

    /**
     * Update user information - Only OFFICER can access
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse updatedUser = userManagementService.updateUser(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Lock or unlock user account - Only OFFICER can access
     */
    @PatchMapping("/{userId}/lock")
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<UserResponse> lockOrUnlockUser(
            @PathVariable Long userId,
            @Valid @RequestBody LockUserRequest request) {
        UserResponse updatedUser = userManagementService.lockOrUnlockUser(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Update smartEkycEnabled and faceRecognitionEnabled - OFFICER or the user themself (CUSTOMER also allowed for own record)
     */
    @PatchMapping("/{userId}/settings")
    @PreAuthorize("hasRole('OFFICER') or (hasRole('CUSTOMER') and #userId == authentication.principal.userId)")
    public ResponseEntity<UserResponse> updateSmartFlags(
            @PathVariable Long userId,
            @Valid @RequestBody SmartFlagsRequest request) {
        UserResponse updated = userManagementService.updateSmartFlags(userId, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Update smatOTP - allowed only when smartEkycEnabled is true; OFFICER or the user themself (CUSTOMER allowed for own record)
     */
    @PatchMapping("/{userId}/smart-otp")
    @PreAuthorize("hasRole('OFFICER') or (hasRole('CUSTOMER') and #userId == authentication.principal.userId)")
    public ResponseEntity<UserResponse> updateSmartOtp(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateSmartOtpRequest request) {
        UserResponse updated = userManagementService.updateSmartOtp(userId, request);
        return ResponseEntity.ok(updated);
    }
}
