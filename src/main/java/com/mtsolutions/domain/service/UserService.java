package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.application.exception.ExpiredEmailVerificationTokenException;
import com.mtsolutions.application.exception.InvalidEmailVerificationTokenException;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.exception.UserAlreadyExistsException;
import com.mtsolutions.application.exception.UserNotFoundException;
import com.mtsolutions.domain.dto.request.CreateUserRequestDto;
import com.mtsolutions.domain.entity.User;
import com.mtsolutions.domain.model.Address;
import com.mtsolutions.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Locale;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AddressService addressService;
    private final PasswordService passwordService;
    private final EmailService emailService;
    private final UtilsService utilsService;
    private final DateComponent dateComponent;
    private final long emailVerificationTokenExpiresMinutes;

    public UserService(UserRepository userRepository,
                       AddressService addressService, PasswordService passwordService,
                       EmailService emailService,
                       UtilsService utilsService,
                       DateComponent dateComponent,
                       @ConfigProperty(name = "app.email.verification-token-expires-minutes") long emailVerificationTokenExpiresMinutes) {
        this.userRepository = userRepository;
        this.addressService = addressService;
        this.passwordService = passwordService;
        this.emailService = emailService;
        this.utilsService = utilsService;
        this.dateComponent = dateComponent;
        this.emailVerificationTokenExpiresMinutes = emailVerificationTokenExpiresMinutes;
    }

    @Transactional
    public User createUser(CreateUserRequestDto request) throws RentlixApiException {
        log.info("Creating user with email: {}", request.email());

        if (this.userRepository.existsByEmailOrCpfOrPhoneOrRg(request.email(), request.cpf(), request.phone(), request.rg())) {
            throw new UserAlreadyExistsException(Features.CREATE_USER);
        }

        Address address = this.addressService.getAddress(request.address());
        String encryptedPassword = this.passwordService.encryptPassword(request.password());

        User user = User.builder()
                .name(request.name())
                .birthDate(request.birthDate())
                .phone(request.phone())
                .cpf(request.cpf())
                .rg(request.rg())
                .occupation(request.occupation())
                .maritalStatus(request.maritalStatus())
                .address(address)
                .email(request.email())
                .emailVerificationToken(null)
                .emailVerificationTokenExpiresAt(null)
                .emailVerified(false)
                .password(encryptedPassword)
                .passwordResetToken(null)
                .passwordResetTokenExpiresAt(null)
                .pictureUrl(request.pictureUrl())
                .documents(request.userDocument())
                .createdAt(this.dateComponent.now())
                .updatedAt(this.dateComponent.now())
                .active(false)
                .build();

        this.userRepository.persist(user);
        log.info("User created with ID: {}", user.getUserId());

        this.emailService.sendWelcomeEmail(user.getName(), user.getEmail());
        this.sendEmailVerification(user);
        return user;
    }

    public User getUserById(UUID userId) throws RentlixApiException {
        return this.userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new UserNotFoundException(Features.GET_USER));
    }

    // Email verification methods
    @Transactional
    public void sendEmailVerification(UUID userId) throws RentlixApiException {
        User user = this.userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new UserNotFoundException(Features.SEND_EMAIL_VERIFICATION));
        this.sendEmailVerification(user);
    }

    @Transactional
    public void sendEmailVerification(User user) {
        log.info("Preparing email verification for user: {}", user.getEmail());

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            log.info("Email already verified for user: {}", user.getEmail());
            return;
        }

        String token = this.utilsService.generateVerificationCode();
        user.setEmailVerificationToken(token);
        user.setEmailVerificationTokenExpiresAt(this.dateComponent.now().plusMinutes(this.emailVerificationTokenExpiresMinutes));
        user.setUpdatedAt(this.dateComponent.now());
        this.userRepository.persist(user);

        this.emailService.sendVerificationEmail(user.getName(), user.getEmail(), token, this.emailVerificationTokenExpiresMinutes);
        log.info("Email verification code generated for user: {}", user.getEmail());
    }

    @Transactional
    public void verifyEmail(String token) throws RentlixApiException {
        log.info("Verifying email");

        if (token == null || token.isBlank()) {
            throw new InvalidEmailVerificationTokenException(Features.VERIFY_EMAIL);
        }

        token = token.trim().toUpperCase(Locale.ROOT);
        User user = this.userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new InvalidEmailVerificationTokenException(Features.VERIFY_EMAIL));

        if (user.getEmailVerificationTokenExpiresAt() == null || this.dateComponent.now().isAfter(user.getEmailVerificationTokenExpiresAt())) {
            log.warn("Expired verification token for user: {}", user.getEmail());
            throw new ExpiredEmailVerificationTokenException(Features.VERIFY_EMAIL);
        }

        user.setEmailVerified(true);
        user.setActive(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiresAt(null);
        user.setUpdatedAt(this.dateComponent.now());
        this.userRepository.persist(user);

        log.info("Email verified for user: {}", user.getEmail());
    }
}
