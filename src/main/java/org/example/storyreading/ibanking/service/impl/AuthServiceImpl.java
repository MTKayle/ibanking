package org.example.storyreading.ibanking.service.impl;

import org.example.storyreading.ibanking.dto.AuthResponse;
import org.example.storyreading.ibanking.dto.LoginRequest;
import org.example.storyreading.ibanking.dto.RegisterRequest;
import org.example.storyreading.ibanking.entity.User;
import org.example.storyreading.ibanking.exception.AccountLockedException;
import org.example.storyreading.ibanking.exception.FaceAuthenticationFailedException;
import org.example.storyreading.ibanking.exception.ResourceAlreadyExistsException;
import org.example.storyreading.ibanking.exception.ResourceNotFoundException;
import org.example.storyreading.ibanking.repository.UserRepository;
import org.example.storyreading.ibanking.service.AuthService;
import org.example.storyreading.ibanking.service.CloudinaryService;
import org.example.storyreading.ibanking.service.FaceRecognitionService;
import org.example.storyreading.ibanking.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired(required = false)
    private FaceRecognitionService faceRecognitionService;

    @Autowired(required = false)
    private CloudinaryService cloudinaryService;

    @Value("${faceplus.confidence.threshold:70.0}")
    private double confidenceThreshold;

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
        user.setPhotoUrl(null); // Explicitly set photoUrl to null
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
    @Transactional
    public AuthResponse registerWithFaceVerification(RegisterRequest registerRequest,
                                                     MultipartFile cccdPhoto,
                                                     MultipartFile selfiePhoto) throws Exception {
        // Kiểm tra service có sẵn không
        if (faceRecognitionService == null) {
            throw new IllegalStateException("Face recognition service chưa được cấu hình. Vui lòng cấu hình Face++ API key.");
        }

        if (cloudinaryService == null) {
            throw new IllegalStateException("Cloudinary service chưa được cấu hình. Vui lòng cấu hình Cloudinary credentials.");
        }

        // Bước 1: Kiểm tra các thông tin đã tồn tại chưa
        if (userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new ResourceAlreadyExistsException("Số điện thoại đã được sử dụng: " + registerRequest.getPhone());
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Email đã được sử dụng: " + registerRequest.getEmail());
        }

        if (userRepository.existsByCccdNumber(registerRequest.getCccdNumber())) {
            throw new ResourceAlreadyExistsException("Số CCCD đã được sử dụng: " + registerRequest.getCccdNumber());
        }

        // Bước 2: Gọi Face++ API để so sánh khuôn mặt
        double confidence = faceRecognitionService.compareFaces(cccdPhoto, selfiePhoto);

//        double confidence = 80;
        if (confidence < confidenceThreshold) {
            throw new FaceAuthenticationFailedException(
                    String.format("Xác thực khuôn mặt thất bại. Độ tương đồng: %.2f%% (yêu cầu >= %.2f%%)",
                            confidence, confidenceThreshold)
            );
        }

        // Bước 3: Tạo user mới
        User user = new User();
        user.setPhone(registerRequest.getPhone());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setCccdNumber(registerRequest.getCccdNumber());
        user.setDateOfBirth(registerRequest.getDateOfBirth());
        user.setPermanentAddress(registerRequest.getPermanentAddress());
        user.setTemporaryAddress(registerRequest.getTemporaryAddress());
        user.setRole(User.Role.customer);

        User savedUser = userRepository.save(user);

        // Bước 4: Upload ảnh selfie lên Cloudinary và lưu URL
        try {
            String selfieUrl = cloudinaryService.uploadImage(selfiePhoto, savedUser.getUserId());
            savedUser.setPhotoUrl(selfieUrl);
            userRepository.save(savedUser);
        } catch (Exception e) {
            System.err.println("Warning: Failed to upload selfie to Cloudinary: " + e.getMessage());
            throw new IllegalStateException("Đăng ký thất bại do lỗi lưu ảnh selfie: " + e.getMessage());
        }

        // Bước 5: Generate JWT token
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
        // Get user first to check if account is locked
        User user = userRepository.findByPhone(loginRequest.getPhone())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with phone: " + loginRequest.getPhone()));

        // Check if account is locked
        if (user.getIsLocked() != null && user.getIsLocked()) {
            throw new AccountLockedException("Account is locked. Please contact support.");
        }

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

        return new AuthResponse(
                token,
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole().name()
        );
    }

    @Override
    @Transactional
    public AuthResponse loginWithFaceRecognition(String phone, MultipartFile facePhoto) throws Exception {
        // Kiểm tra service có sẵn không
        if (faceRecognitionService == null) {
            throw new IllegalStateException("Face recognition service chưa được cấu hình. Vui lòng cấu hình Face++ API key.");
        }

        // Bước 1: Tìm user theo phone
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với số điện thoại: " + phone));

        // Bước 2: Kiểm tra account có bị khóa không
        if (user.getIsLocked() != null && user.getIsLocked()) {
            throw new AccountLockedException("Tài khoản đã bị khóa. Vui lòng liên hệ hỗ trợ.");
        }

        // Bước 3: Kiểm tra face_recognition_enabled
        if (user.getFaceRecognitionEnabled() == null || !user.getFaceRecognitionEnabled()) {
            throw new IllegalStateException("Tính năng đăng nhập bằng khuôn mặt chưa được kích hoạt cho tài khoản này. Vui lòng kích hoạt trong cài đặt.");
        }

        // Bước 4: Kiểm tra user đã có photoUrl chưa
        if (user.getPhotoUrl() == null || user.getPhotoUrl().isEmpty()) {
            throw new IllegalStateException("Tài khoản chưa có ảnh khuôn mặt đã lưu. Vui lòng đăng ký lại với xác thực khuôn mặt.");
        }

        // Bước 5: So sánh khuôn mặt với photoUrl (gọi Face++ API)
        double confidence = faceRecognitionService.compareFaceWithUrl(facePhoto, user.getPhotoUrl());

        // Bước 6: Kiểm tra confidence threshold
        if (confidence < confidenceThreshold) {
            throw new FaceAuthenticationFailedException(
                    String.format("Xác thực khuôn mặt thất bại. Độ tương đồng: %.2f%% (yêu cầu >= %.2f%%)",
                            confidence, confidenceThreshold)
            );
        }

        // Bước 7: Tạo authentication token (không cần password vì đã xác thực bằng face)
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getPhone(),
                null,
                org.springframework.security.core.authority.AuthorityUtils.createAuthorityList("ROLE_" + user.getRole().name().toUpperCase())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Bước 8: Generate JWT token
        String token = tokenProvider.generateTokenFromPhone(user.getPhone());

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
