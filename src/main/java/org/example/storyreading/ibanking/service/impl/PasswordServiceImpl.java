package org.example.storyreading.ibanking.service.impl;

import org.example.storyreading.ibanking.dto.ChangePasswordRequest;
import org.example.storyreading.ibanking.dto.ChangePasswordResponse;
import org.example.storyreading.ibanking.entity.User;
import org.example.storyreading.ibanking.repository.UserRepository;
import org.example.storyreading.ibanking.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordServiceImpl implements PasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        // Validate input
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }

        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống");
        }

        if (request.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        }

        // Tìm user theo phone
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với số điện thoại: " + request.getPhone()));

        // Kiểm tra tài khoản có bị khóa không
        if (user.getIsLocked() != null && user.getIsLocked()) {
            throw new RuntimeException("Tài khoản đã bị khóa, không thể đổi mật khẩu");
        }

        // Hash mật khẩu mới
        String hashedPassword = passwordEncoder.encode(request.getNewPassword());

        // Update mật khẩu
        user.setPasswordHash(hashedPassword);
        userRepository.save(user);

        return new ChangePasswordResponse(true, "Đổi mật khẩu thành công");
    }
}

