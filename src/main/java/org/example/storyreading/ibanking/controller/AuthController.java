package org.example.storyreading.ibanking.controller;

import jakarta.validation.Valid;
import org.example.storyreading.ibanking.dto.AuthResponse;
import org.example.storyreading.ibanking.dto.LoginRequest;
import org.example.storyreading.ibanking.dto.RegisterRequest;
import org.example.storyreading.ibanking.dto.RefreshTokenRequest;
import org.example.storyreading.ibanking.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = authService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/register-with-face", consumes = {"multipart/form-data"})
    public ResponseEntity<AuthResponse> registerWithFace(
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("fullName") String fullName,
            @RequestParam("cccdNumber") String cccdNumber,
            @RequestParam(value = "dateOfBirth", required = false) String dateOfBirth,
            @RequestParam(value = "permanentAddress", required = false) String permanentAddress,
            @RequestParam(value = "temporaryAddress", required = false) String temporaryAddress,
            @RequestPart("cccdPhoto") MultipartFile cccdPhoto,
            @RequestPart("selfiePhoto") MultipartFile selfiePhoto) throws Exception {

        // Validate file types
        if (!isImageFile(cccdPhoto) || !isImageFile(selfiePhoto)) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh (jpg, jpeg, png)");
        }

        // Validate file sizes (max 5MB each)
        if (cccdPhoto.getSize() > 5 * 1024 * 1024 || selfiePhoto.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước ảnh không được vượt quá 5MB");
        }

        // Create RegisterRequest from form data
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPhone(phone);
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        registerRequest.setFullName(fullName);
        registerRequest.setCccdNumber(cccdNumber);

        if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
            registerRequest.setDateOfBirth(java.time.LocalDate.parse(dateOfBirth));
        }
        registerRequest.setPermanentAddress(permanentAddress);
        registerRequest.setTemporaryAddress(temporaryAddress);

        // Call service to register with face verification
        AuthResponse response = authService.registerWithFaceVerification(
                registerRequest, cccdPhoto, selfiePhoto);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Đăng nhập bằng nhận diện khuôn mặt
     * Frontend cần gửi ảnh và số điện thoại, backend sẽ so sánh với user có số điện thoại đó
     */
    @PostMapping(value = "/login-with-face", consumes = {"multipart/form-data"})
    public ResponseEntity<AuthResponse> loginWithFace(
            @RequestParam("phone") String phone,
            @RequestPart("facePhoto") MultipartFile facePhoto) throws Exception {

        // Validate file type
        if (!isImageFile(facePhoto)) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh (jpg, jpeg, png)");
        }

        // Validate file size (max 5MB)
        if (facePhoto.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước ảnh không được vượt quá 5MB");
        }

        // Call service to authenticate with face (cần phone để xác định user)
        AuthResponse response = authService.loginWithFaceRecognition(phone, facePhoto);

        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token bằng refresh token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        AuthResponse response = authService.refreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png")
        );
    }
}
