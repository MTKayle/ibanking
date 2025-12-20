package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.AuthResponse;
import org.example.storyreading.ibanking.dto.LoginRequest;
import org.example.storyreading.ibanking.dto.RegisterRequest;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {

    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse registerWithFaceVerification(RegisterRequest registerRequest,
                                              MultipartFile cccdPhoto,
                                              MultipartFile selfiePhoto) throws Exception;

    AuthResponse login(LoginRequest loginRequest);

    AuthResponse loginWithFaceRecognition(String phone, MultipartFile facePhoto) throws Exception;

    AuthResponse refreshToken(String refreshToken);

    boolean isFingerprintLoginEnabledByPhone(String phone);

    boolean checkPhoneExists(String phone);

    boolean checkCccdExists(String cccd);
}
