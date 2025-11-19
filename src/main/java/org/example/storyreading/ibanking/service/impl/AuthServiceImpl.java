package org.example.storyreading.ibanking.service.impl;

import org.example.storyreading.ibanking.dto.AuthResponse;
import org.example.storyreading.ibanking.dto.LoginRequest;
import org.example.storyreading.ibanking.dto.RegisterRequest;
import org.example.storyreading.ibanking.entity.User;
import org.example.storyreading.ibanking.exception.ResourceAlreadyExistsException;
import org.example.storyreading.ibanking.repository.UserRepository;
import org.example.storyreading.ibanking.service.AuthService;
import org.example.storyreading.ibanking.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if phone already exists (primary)
        if (userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new ResourceAlreadyExistsException("Phone number already in use: " + registerRequest.getPhone());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already in use: " + registerRequest.getEmail());
        }

        // Check if CCCD already exists
        if (userRepository.existsByCccdNumber(registerRequest.getCccdNumber())) {
            throw new ResourceAlreadyExistsException("CCCD number already in use: " + registerRequest.getCccdNumber());
        }

        // Create new user
        User user = new User();
        user.setPhone(registerRequest.getPhone());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setCccdNumber(registerRequest.getCccdNumber());
        user.setDateOfBirth(registerRequest.getDateOfBirth());
        user.setPermanentAddress(registerRequest.getPermanentAddress());
        user.setTemporaryAddress(registerRequest.getTemporaryAddress());
        user.setRole(User.Role.customer); // Default role

        User savedUser = userRepository.save(user);

        // Generate JWT token using phone
        String token = tokenProvider.generateTokenFromPhone(savedUser.getPhone());

        return new AuthResponse(
                token,
                savedUser.getUserId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getPhone(),
                savedUser.getRole().name()
        );
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        // Authenticate user - only phone is accepted
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getPhone(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = tokenProvider.generateToken(authentication);

        // Get user details by phone only
        User user = userRepository.findByPhone(loginRequest.getPhone())
                .orElseThrow(() -> new RuntimeException("User not found with phone: " + loginRequest.getPhone()));

        return new AuthResponse(
                token,
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole().name()
        );
    }
}
