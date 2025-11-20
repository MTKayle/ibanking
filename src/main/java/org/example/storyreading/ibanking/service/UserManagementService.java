package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.LockUserRequest;
import org.example.storyreading.ibanking.dto.UpdateUserRequest;
import org.example.storyreading.ibanking.dto.UserResponse;
import org.example.storyreading.ibanking.entity.User;
import org.example.storyreading.ibanking.exception.ResourceAlreadyExistsException;
import org.example.storyreading.ibanking.exception.ResourceNotFoundException;
import org.example.storyreading.ibanking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        // Return only users with role = customer
        return userRepository.findAllByRole(User.Role.customer).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Update fields if provided
        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Check if email already exists for another user
            userRepository.findByEmail(request.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getUserId().equals(userId)) {
                    throw new ResourceAlreadyExistsException("Email already in use");
                }
            });
            user.setEmail(request.getEmail());
        }

        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getPermanentAddress() != null) {
            user.setPermanentAddress(request.getPermanentAddress());
        }

        if (request.getTemporaryAddress() != null) {
            user.setTemporaryAddress(request.getTemporaryAddress());
        }

        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    @Transactional
    public UserResponse lockOrUnlockUser(Long userId, LockUserRequest request) {
        if (request == null || request.getLocked() == null) {
            throw new IllegalArgumentException("'locked' flag must be provided and be true or false");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setIsLocked(request.getLocked());
        User updatedUser = userRepository.save(user);

        return mapToUserResponse(updatedUser);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setCccdNumber(user.getCccdNumber());
        response.setPermanentAddress(user.getPermanentAddress());
        response.setTemporaryAddress(user.getTemporaryAddress());
        response.setPhotoUrl(user.getPhotoUrl());
        response.setRole(user.getRole().name());
        response.setIsLocked(user.getIsLocked());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
