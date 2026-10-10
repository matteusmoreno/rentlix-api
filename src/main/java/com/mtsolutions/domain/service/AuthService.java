package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.exception.AccountNotActiveException;
import com.mtsolutions.application.exception.InvalidCredentialsException;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.dto.request.LoginRequestDto;
import com.mtsolutions.domain.dto.response.LoginResponseDto;
import com.mtsolutions.domain.entity.User;
import com.mtsolutions.domain.model.TokenResult;
import com.mtsolutions.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public AuthService(JwtService jwtService, UserRepository userRepository, PasswordService passwordService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto request) throws RentlixApiException {
        log.info("Attempting to authenticate user with email: {}", request.email());

        User user = this.userRepository.findByEmailOptional(request.email())
                .orElseThrow(() -> new InvalidCredentialsException(Features.LOGIN));

        if (!this.passwordService.verifyPassword(request.password(), user.getPassword())) {
            log.warn("Invalid credentials for email: {}", request.email());
            throw new InvalidCredentialsException(Features.LOGIN);
        }

        if (!Boolean.TRUE.equals(user.getActive())) {
            log.warn("Inactive account login attempt for email: {}", request.email());
            throw new AccountNotActiveException(Features.LOGIN);
        }

        log.info("User authenticated successfully: {}", user.getEmail());
        TokenResult tokenResult = this.jwtService.generateToken(user);
        return new LoginResponseDto(tokenResult);
    }
}
