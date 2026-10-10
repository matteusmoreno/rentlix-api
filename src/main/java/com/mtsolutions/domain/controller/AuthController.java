package com.mtsolutions.domain.controller;

import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.dto.request.LoginRequestDto;
import com.mtsolutions.domain.dto.response.LoginResponseDto;
import com.mtsolutions.domain.service.AuthService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public LoginResponseDto login(LoginRequestDto request) throws RentlixApiException {
        return authService.login(request);
    }
}
