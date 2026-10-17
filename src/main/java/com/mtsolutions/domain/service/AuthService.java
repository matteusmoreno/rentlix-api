package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.application.exception.AccountNotActiveException;
import com.mtsolutions.application.exception.ExpiredPasswordResetCodeException;
import com.mtsolutions.application.exception.InvalidCredentialsException;
import com.mtsolutions.application.exception.InvalidPasswordResetCodeException;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.dto.request.ForgotPasswordRequestDto;
import com.mtsolutions.domain.dto.request.LoginRequestDto;
import com.mtsolutions.domain.dto.request.ResetPasswordRequestDto;
import com.mtsolutions.domain.dto.response.LoginResponseDto;
import com.mtsolutions.domain.entity.User;
import com.mtsolutions.domain.model.TokenResult;
import com.mtsolutions.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Locale;

@ApplicationScoped
@Slf4j
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final EmailService emailService;
    private final UtilsService utilsService;
    private final DateComponent dateComponent;
    private final long passwordResetTokenExpiresMinutes;

    public AuthService(JwtService jwtService,
                       UserRepository userRepository,
                       PasswordService passwordService,
                       EmailService emailService,
                       UtilsService utilsService,
                       DateComponent dateComponent,
                       @ConfigProperty(name = "app.email.password-reset-token-expires-minutes") long passwordResetTokenExpiresMinutes) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.emailService = emailService;
        this.utilsService = utilsService;
        this.dateComponent = dateComponent;
        this.passwordResetTokenExpiresMinutes = passwordResetTokenExpiresMinutes;
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

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDto request) {
        log.info("Password reset requested for email: {}", request.email());
        User user = this.userRepository.findByEmailOptional(request.email()).orElse(null);
        if (user == null) {
            return;
        }

        String code = this.utilsService.generateVerificationCode();
        user.setPasswordResetToken(code);
        user.setPasswordResetTokenExpiresAt(this.dateComponent.now().plusMinutes(this.passwordResetTokenExpiresMinutes));
        user.setUpdatedAt(this.dateComponent.now());
        this.userRepository.persist(user);
        this.emailService.sendPasswordResetEmail(user.getName(), user.getEmail(), code, this.passwordResetTokenExpiresMinutes);
        log.info("Password reset code generated for user: {}", user.getEmail());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDto request) throws RentlixApiException {
        log.info("Resetting password for email: {}", request.email());
        String code = request.code().trim().toUpperCase(Locale.ROOT);
        User user = this.userRepository.findByEmailAndPasswordResetToken(request.email(), code)
                .orElseThrow(() -> new InvalidPasswordResetCodeException(Features.RESET_PASSWORD));

        if (user.getPasswordResetTokenExpiresAt() == null || this.dateComponent.now().isAfter(user.getPasswordResetTokenExpiresAt())) {
            log.warn("Expired password reset code for user: {}", user.getEmail());
            throw new ExpiredPasswordResetCodeException(Features.RESET_PASSWORD);
        }

        user.setPassword(this.passwordService.encryptPassword(request.password()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiresAt(null);
        user.setUpdatedAt(this.dateComponent.now());
        this.userRepository.persist(user);
        log.info("Password reset for user: {}", user.getEmail());
    }
}
