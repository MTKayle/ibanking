package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.ChangePasswordRequest;
import org.example.storyreading.ibanking.dto.ChangePasswordResponse;

public interface PasswordService {
    /**
     * Đổi mật khẩu user dựa vào số điện thoại
     * @param request chứa phone và newPassword
     * @return ChangePasswordResponse
     */
    ChangePasswordResponse changePassword(ChangePasswordRequest request);
}

