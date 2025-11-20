package org.example.storyreading.ibanking.controller;

import jakarta.validation.Valid;
import org.example.storyreading.ibanking.dto.LockUserRequest;
import org.example.storyreading.ibanking.dto.UpdateUserRequest;
import org.example.storyreading.ibanking.dto.UserResponse;
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
}

