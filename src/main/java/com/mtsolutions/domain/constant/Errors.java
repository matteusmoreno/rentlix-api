package com.mtsolutions.domain.constant;

import lombok.Getter;

@Getter
public enum Errors {
    INVALID_REQUEST("The request is invalid"),
    USER_ALREADY_EXISTS("User already exists"),
    COMPANY_ALREADY_EXISTS("Company already exists"),
    COMPANY_NOT_FOUND("Company not found"),
    COMPANY_STATUS_CONFLICT("Company status does not allow this action"),
    COMPANY_MEMBER_ALREADY_EXISTS("Company member already exists"),
    ASSET_ALREADY_EXISTS("Asset already exists"),
    ASSET_NOT_FOUND("Asset not found"),
    ASSET_STATUS_CONFLICT("Asset status does not allow this action"),
    GALLERY_IMAGE_NOT_FOUND("Gallery image not found"),
    ASSET_DETAILS_INVALID("Asset details do not match the type"),
    MEMBER_ROLE_INVALID("Member role is invalid"),
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
