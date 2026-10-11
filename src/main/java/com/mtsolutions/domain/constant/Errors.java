package com.mtsolutions.domain.constant;

import lombok.Getter;

@Getter
public enum Errors {
    INVALID_REQUEST("The request is invalid"),
    USER_ALREADY_EXISTS("User already exists"),
    USER_NOT_FOUND("User not found"),
    INVALID_CREDENTIALS("Invalid credentials"),
    ACCOUNT_NOT_ACTIVE("Account is not active"),
    EMAIL_VERIFICATION_TOKEN_INVALID("Email verification token is invalid"),
    EMAIL_VERIFICATION_TOKEN_EXPIRED("Email verification token is expired"),
    ZIP_CODE_NOT_FOUND("No address found for the given zip code"),
    VIA_CEP_UNAVAILABLE("ViaCEP is unavailable"),
    PICTURE_NOT_FOUND("Picture not found"),
    DOCUMENT_NOT_FOUND("Document not found"),
    DOCUMENT_TYPE_INVALID("Document type is invalid"),
    ACCESS_DENIED("Access denied"),
    CLOUDINARY_UNAVAILABLE("Cloudinary is unavailable");

    private final String description;

    Errors(String description) {
        this.description = description;
    }
}
