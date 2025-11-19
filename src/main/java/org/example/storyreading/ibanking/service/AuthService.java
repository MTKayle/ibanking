package org.example.storyreading.ibanking.service;

import org.example.storyreading.ibanking.dto.AuthResponse;
import org.example.storyreading.ibanking.dto.LoginRequest;
import org.example.storyreading.ibanking.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);
}
