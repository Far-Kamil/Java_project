package com.needed.task.service;

import org.springframework.http.ResponseEntity;

import com.needed.task.dto.LoginRequest;
import com.needed.task.dto.LoginResponse;
import com.needed.task.dto.UserLoggedDto;

public interface AuthService {
    ResponseEntity<LoginResponse> login(LoginRequest loginRequest, String accessToken, String refreshToken);

    ResponseEntity<LoginResponse> refresh(String refreshToken);

    ResponseEntity<LoginResponse> logout(String accessToken, String refreshToken);

    UserLoggedDto getUserLoggedInfo();
}
