package com.mtsolutions.domain.controller;

import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.dto.request.CreateUserRequestDto;
import com.mtsolutions.domain.entity.User;
import com.mtsolutions.domain.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public User createUser(CreateUserRequestDto request) throws RentlixApiException {
        return this.userService.createUser(request);
    }

    public User getUserById(UUID userId) throws RentlixApiException {
        return this.userService.getUserById(userId);
    }

    public void sendEmailVerification(UUID userId) throws RentlixApiException {
        this.userService.sendEmailVerification(userId);
    }

    public void verifyEmail(String token) throws RentlixApiException {
        this.userService.verifyEmail(token);
    }

    public User uploadPicture(UUID userId, byte[] picture) throws RentlixApiException {
        return this.userService.uploadPicture(userId, picture);
    }

    public User deletePicture(UUID userId) throws RentlixApiException {
        return this.userService.deletePicture(userId);
    }

    public User uploadDocuments(UUID userId, Map<String, byte[]> documents) throws RentlixApiException {
        return this.userService.uploadDocuments(userId, documents);
    }

    public User deleteDocument(UUID userId, String documentType) throws RentlixApiException {
        return this.userService.deleteDocuments(userId, List.of(documentType));
    }
}
