package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.component.ContextComponent;
import com.mtsolutions.application.component.ContextUser;
import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.application.exception.AccessDeniedException;
import com.mtsolutions.application.exception.DocumentNotFoundException;
import com.mtsolutions.application.exception.ExpiredEmailVerificationTokenException;
import com.mtsolutions.application.exception.InvalidDocumentTypeException;
import com.mtsolutions.application.exception.InvalidEmailVerificationTokenException;
import com.mtsolutions.application.exception.PictureNotFoundException;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.exception.UserAlreadyExistsException;
import com.mtsolutions.application.exception.UserNotFoundException;
import com.mtsolutions.domain.constant.CloudinaryFolder;
import com.mtsolutions.domain.dto.request.CreateUserRequestDto;
import com.mtsolutions.domain.dto.request.UpdateUserRequestDto;
import com.mtsolutions.domain.entity.User;
import com.mtsolutions.domain.model.Address;
import com.mtsolutions.domain.model.UserDocument;
import com.mtsolutions.domain.model.UserPage;
import com.mtsolutions.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class UserService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final UserRepository userRepository;
    private final AddressService addressService;
    private final PasswordService passwordService;
    private final EmailService emailService;
    private final UtilsService utilsService;
    private final CloudinaryService cloudinaryService;
    private final ContextComponent contextComponent;
    private final DateComponent dateComponent;
    private final long emailVerificationTokenExpiresMinutes;

    public UserService(UserRepository userRepository,
                       AddressService addressService, PasswordService passwordService,
                       EmailService emailService,
                       UtilsService utilsService,
                       CloudinaryService cloudinaryService,
                       ContextComponent contextComponent,
                       DateComponent dateComponent,
                       @ConfigProperty(name = "app.email.verification-token-expires-minutes") long emailVerificationTokenExpiresMinutes) {
        this.userRepository = userRepository;
        this.addressService = addressService;
        this.passwordService = passwordService;
        this.emailService = emailService;
        this.utilsService = utilsService;
        this.cloudinaryService = cloudinaryService;
        this.contextComponent = contextComponent;
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
                .pictureUrl(null)
                .documents(null)
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
        this.requireSameUser(userId, Features.GET_USER);
        return this.userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new UserNotFoundException(Features.GET_USER));
    }

    public UserPage listUsers(int page, int size) {
        int currentPage = page;
        int pageSize = size;

        if (currentPage < 0) {
            currentPage = 0;
        }

        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }

        List<User> users = this.userRepository.findPage(currentPage, pageSize);
        long totalElements = this.userRepository.count();
        return new UserPage(users, currentPage, pageSize, totalElements);
    }

    @Transactional
    public User updateUser(UpdateUserRequestDto request, UUID userId) throws RentlixApiException {
        log.info("Updating user: {}", userId);
        this.requireSameUser(userId, Features.UPDATE_USER);

        User user = this.userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new UserNotFoundException(Features.UPDATE_USER));

        if (this.userRepository.existsOtherByCpfOrPhoneOrRg(userId, request.cpf(), request.phone(), request.rg())) {
            throw new UserAlreadyExistsException(Features.UPDATE_USER);
        }

        if (request.name() != null) user.setName(request.name());
        if (request.birthDate() != null) user.setBirthDate(request.birthDate());
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.cpf() != null) user.setCpf(request.cpf());
        if (request.rg() != null) user.setRg(request.rg());
        if (request.occupation() != null) user.setOccupation(request.occupation());
        if (request.maritalStatus() != null) user.setMaritalStatus(request.maritalStatus());
        if (request.address() != null) {
            Address address = this.addressService.getAddress(request.address());
            user.setAddress(address);
        }

        user.setUpdatedAt(this.dateComponent.now());
        this.userRepository.persist(user);
        log.info("User updated: {}", user.getUserId());
        return user;
    }

    @Transactional
    public User uploadPicture(UUID userId, byte[] fileBytes) throws RentlixApiException {
        this.requireSameUser(userId, Features.UPLOAD_USER_PICTURE);
        User user = this.userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new UserNotFoundException(Features.UPLOAD_USER_PICTURE));

        if (user.getPictureUrl() != null && !user.getPictureUrl().isBlank()) {
            String oldPublicId = this.cloudinaryService.extractPublicId(user.getPictureUrl());
            if (oldPublicId != null) {
                this.cloudinaryService.delete(oldPublicId, CloudinaryFolder.USER_PICTURE.getResourceType(), Features.UPLOAD_USER_PICTURE);
            }
        }

        String url = this.cloudinaryService.upload(fileBytes, user.getUserId().toString(), CloudinaryFolder.USER_PICTURE, Features.UPLOAD_USER_PICTURE);
        user.setPictureUrl(url);
        user.setUpdatedAt(this.dateComponent.now());
        this.userRepository.persist(user);

        log.info("Picture uploaded for user: {}", user.getUserId());
        return user;
    }

    @Transactional
    public User deletePicture(UUID userId) throws RentlixApiException {
        this.requireSameUser(userId, Features.DELETE_USER_PICTURE);
        User user = this.userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new UserNotFoundException(Features.DELETE_USER_PICTURE));

        if (user.getPictureUrl() == null || user.getPictureUrl().isBlank()) {
            throw new PictureNotFoundException(Features.DELETE_USER_PICTURE);
        }

        String publicId = this.cloudinaryService.extractPublicId(user.getPictureUrl());
        if (publicId != null) {
            this.cloudinaryService.delete(publicId, CloudinaryFolder.USER_PICTURE.getResourceType(), Features.DELETE_USER_PICTURE);
        }
        user.setPictureUrl(null);
        user.setUpdatedAt(this.dateComponent.now());
        this.userRepository.persist(user);

        log.info("Picture deleted for user: {}", userId);
        return user;
    }

    @Transactional
    public User uploadDocuments(UUID userId, Map<String, byte[]> documents) throws RentlixApiException {
        this.requireSameUser(userId, Features.UPLOAD_USER_DOCUMENT);
        User user = this.userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new UserNotFoundException(Features.UPLOAD_USER_DOCUMENT));

        if (user.getDocuments() == null) {
            user.setDocuments(new UserDocument());
        }

        for (Map.Entry<String, byte[]> entry : documents.entrySet()) {
            String documentType = entry.getKey().toLowerCase(Locale.ROOT);
            String existingUrl = this.documentUrl(user.getDocuments(), documentType, Features.UPLOAD_USER_DOCUMENT);
            if (existingUrl != null && !existingUrl.isBlank()) {
                String publicId = this.cloudinaryService.extractPublicId(existingUrl);
                if (publicId != null) {
                    this.cloudinaryService.delete(publicId, CloudinaryFolder.USER_DOCUMENT.getResourceType(), Features.UPLOAD_USER_DOCUMENT);
                }
            }

            String url = this.cloudinaryService.upload(
                    entry.getValue(),
                    user.getUserId() + "_" + documentType,
                    CloudinaryFolder.USER_DOCUMENT,
                    Features.UPLOAD_USER_DOCUMENT
            );
            this.setDocumentUrl(user.getDocuments(), documentType, url, Features.UPLOAD_USER_DOCUMENT);
            log.info("Document {} uploaded for user: {}", documentType, userId);
        }

        user.setUpdatedAt(this.dateComponent.now());
        this.userRepository.persist(user);
        return user;
    }

    @Transactional
    public User deleteDocuments(UUID userId, List<String> documentTypes) throws RentlixApiException {
        this.requireSameUser(userId, Features.DELETE_USER_DOCUMENT);
        User user = this.userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new UserNotFoundException(Features.DELETE_USER_DOCUMENT));

        if (user.getDocuments() == null) {
            throw new DocumentNotFoundException(Features.DELETE_USER_DOCUMENT);
        }

        for (String documentType : documentTypes) {
            String normalizedType = documentType.toLowerCase(Locale.ROOT);
            String existingUrl = this.documentUrl(user.getDocuments(), normalizedType, Features.DELETE_USER_DOCUMENT);
            if (existingUrl == null || existingUrl.isBlank()) {
                throw new DocumentNotFoundException(Features.DELETE_USER_DOCUMENT);
            }

            String publicId = this.cloudinaryService.extractPublicId(existingUrl);
            if (publicId != null) {
                this.cloudinaryService.delete(publicId, CloudinaryFolder.USER_DOCUMENT.getResourceType(), Features.DELETE_USER_DOCUMENT);
            }
            this.setDocumentUrl(user.getDocuments(), normalizedType, null, Features.DELETE_USER_DOCUMENT);
            log.info("Document {} deleted for user: {}", normalizedType, userId);
        }

        user.setUpdatedAt(this.dateComponent.now());
        this.userRepository.persist(user);
        return user;
    }

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

    protected void requireSameUser(UUID userId, Features feature) throws RentlixApiException {
        ContextUser currentUser = this.contextComponent.getCurrentUser();
        if (currentUser == null || !userId.equals(currentUser.getUserId())) {
            throw new AccessDeniedException(feature);
        }
    }

    protected String documentUrl(UserDocument documents, String documentType, Features feature) throws RentlixApiException {
        return switch (documentType) {
            case "cnh" -> documents.getCnhUrl();
            case "cpf" -> documents.getCpfUrl();
            case "rg" -> documents.getRgUrl();
            case "proofofresidence" -> documents.getProofOfResidenceUrl();
            case "criminalrecord" -> documents.getCriminalRecordUrl();
            case "passport" -> documents.getPassportUrl();
            default -> throw new InvalidDocumentTypeException(feature);
        };
    }

    protected void setDocumentUrl(UserDocument documents, String documentType, String url, Features feature) throws RentlixApiException {
        switch (documentType) {
            case "cnh" -> documents.setCnhUrl(url);
            case "cpf" -> documents.setCpfUrl(url);
            case "rg" -> documents.setRgUrl(url);
            case "proofofresidence" -> documents.setProofOfResidenceUrl(url);
            case "criminalrecord" -> documents.setCriminalRecordUrl(url);
            case "passport" -> documents.setPassportUrl(url);
            default -> throw new InvalidDocumentTypeException(feature);
        }
    }
}
