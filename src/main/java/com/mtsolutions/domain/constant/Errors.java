package com.mtsolutions.domain.constant;

import lombok.Getter;

@Getter
public enum Errors {
    INVALID_REQUEST("The request is invalid"),
    ZIP_CODE_NOT_FOUND("No address found for the given zip code"),
    VIA_CEP_UNAVAILABLE("ViaCEP is unavailable");

    private final String description;

    Errors(String description) {
        this.description = description;
    }
}
