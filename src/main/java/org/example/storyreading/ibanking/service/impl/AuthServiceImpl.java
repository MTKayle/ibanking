package org.example.storyreading.ibanking.service.impl;

import org.example.storyreading.ibanking.dto.AuthResponse;
import org.example.storyreading.ibanking.dto.LoginRequest;
import org.example.storyreading.ibanking.dto.RegisterRequest;
import org.example.storyreading.ibanking.entity.Bank;
import org.example.storyreading.ibanking.entity.User;
import org.example.storyreading.ibanking.exception.AccountLockedException;
import org.example.storyreading.ibanking.exception.FaceAuthenticationFailedException;
import org.example.storyreading.ibanking.exception.ResourceAlreadyExistsException;
import org.example.storyreading.ibanking.exception.ResourceNotFoundException;
import org.example.storyreading.ibanking.repository.BankRepository;
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

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

import org.example.storyreading.ibanking.entity.Account;
import org.example.storyreading.ibanking.entity.CheckingAccount;
import org.example.storyreading.ibanking.repository.AccountRepository;
import org.example.storyreading.ibanking.repository.CheckingAccountRepository;

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

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Value("${faceplus.confidence.threshold:70.0}")
    private double confidenceThreshold;

    private final Random random = new Random();
    @Autowired
    private BankRepository bankRepository;

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

       //  Bước 2: Gọi Face++ API để so sánh khuôn mặt
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
        //gan bank co HAT cho user moi dang ky
        Bank bank = bankRepository.findByBankBin("770717")
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngân hàng với Bank Code: 770717"));
        user.setBank(bank);

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

        // Bước 4.5: Tạo tự động một tài khoản checking cho user
        try {
            Account account = new Account();
            account.setUser(savedUser);
            account.setAccountType(Account.AccountType.checking);
            account.setAccountNumber(generateUniqueAccountNumber());
            account.setStatus(Account.Status.active);
            Account savedAccount = accountRepository.save(account);

            CheckingAccount checkingAccount = new CheckingAccount();
            checkingAccount.setAccount(savedAccount);
            checkingAccount.setBalance(BigDecimal.ZERO);
            checkingAccount.setOverdraftLimit(BigDecimal.ZERO);
            checkingAccountRepository.save(checkingAccount);
        } catch (Exception e) {
            // If account creation fails, log and continue (or you may choose to abort registration)
            System.err.println("Warning: Failed to create checking account for user " + savedUser.getUserId() + ": " + e.getMessage());
            // optionally: throw new IllegalStateException("Đăng ký thất bại do lỗi tạo tài khoản: " + e.getMessage());
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

        // Generate JWT tokens
        String token = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshTokenFromAuthentication(authentication);

        return new AuthResponse(
                token,
                refreshToken,
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole().name()
        );
    }

    @Override
    public AuthResponse loginWithFaceRecognition(String phone, MultipartFile facePhoto) {
         // Kiểm tra service có sẵn không
         if (faceRecognitionService == null) {
             throw new IllegalStateException("Face recognition service chưa được cấu hình. Vui lòng cấu hình Face++ API key.");
         }

        // Bước 1: Tìm user theo số điện thoại
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với số điện thoại: " + phone));

        // Bước 2: Kiểm tra user có bị khóa không
        if (user.getIsLocked() != null && user.getIsLocked()) {
            throw new AccountLockedException("Tài khoản đã bị khóa. Vui lòng liên hệ hỗ trợ.");
        }

        // Bước 3: Kiểm tra user có bật tính năng face recognition không
        if (user.getFaceRecognitionEnabled() == null || !user.getFaceRecognitionEnabled()) {
            throw new FaceAuthenticationFailedException("Tài khoản chưa bật tính năng nhận diện khuôn mặt. Vui lòng bật tính năng này trước khi sử dụng.");
        }

        // Bước 4: So sánh face với user được chỉ định
        double confidence;
        try {
            confidence = faceRecognitionService.compareFaceWithUrl(facePhoto, user.getPhotoUrl());
            System.out.printf("User %s (%s) - Confidence: %.2f%%%n",
                    user.getFullName(), user.getPhone(), confidence);
        } catch (Exception e) {
            throw new FaceAuthenticationFailedException("Lỗi khi so sánh khuôn mặt: " + e.getMessage());
        }

        // Bước 5: Kiểm tra confidence threshold
        if (confidence < confidenceThreshold) {
            throw new FaceAuthenticationFailedException(
                    String.format("Xác thực khuôn mặt thất bại. Độ tương đồng: %.2f%% (yêu cầu >= %.2f%%)",
                            confidence, confidenceThreshold)
            );
        }

        System.out.printf("Login successful! User: %s (%s) with confidence: %.2f%%%n",
                user.getFullName(), user.getPhone(), confidence);

        // Bước 6: Tạo authentication token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getPhone(),
                null,
                org.springframework.security.core.authority.AuthorityUtils.createAuthorityList("ROLE_" + user.getRole().name().toUpperCase())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Bước 7: Generate JWT tokens
        String token = tokenProvider.generateTokenFromPhone(user.getPhone());
        String refreshToken = tokenProvider.generateRefreshToken(user.getPhone());

        return new AuthResponse(
                token,
                refreshToken,
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole().name()
        );
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        // Validate refresh token
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        // Check if token is actually a refresh token
        if (!tokenProvider.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Token không phải là refresh token");
        }

        // Get phone from refresh token
        String phone = tokenProvider.getPhoneFromToken(refreshToken);

        // Get user from database
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with phone: " + phone));

        // Check if account is locked
        if (user.getIsLocked() != null && user.getIsLocked()) {
            throw new AccountLockedException("Account is locked. Please contact support.");
        }

        // Generate new access token and refresh token
        String newAccessToken = tokenProvider.generateTokenFromPhone(user.getPhone());
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getPhone());

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole().name()
        );
    }

    /**
     * Generate a reasonably unique account number string.
     * Keeps trying until an unused number is found (very unlikely to loop many times).
     */
    private String generateUniqueAccountNumber() {
        for (int attempts = 0; attempts < 10; attempts++) {
            // Example format: 10-digit numeric
            String number = String.format("%010d", Math.abs(random.nextLong()) % 1_000_000_0000L);
            Optional<Account> existing = accountRepository.findByAccountNumber(number);
            if (existing.isEmpty()) return number;
        }
        // Fallback: use timestamp + random
        return "AC" + System.currentTimeMillis();
    }

    @Override
    public boolean isFingerprintLoginEnabledByPhone(String phone) {
        // Tìm user theo số điện thoại
        Optional<User> userOpt = userRepository.findByPhone(phone);

        // Nếu không tìm thấy user hoặc user chưa bật fingerprint login, return false
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        // Kiểm tra xem user có bật fingerprint login không
        return user.getFingerprintLoginEnabled() != null && user.getFingerprintLoginEnabled();
    }

    @Override
    public boolean checkPhoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public boolean checkCccdExists(String cccd) {
        return userRepository.existsByCccdNumber(cccd);
    }
}
