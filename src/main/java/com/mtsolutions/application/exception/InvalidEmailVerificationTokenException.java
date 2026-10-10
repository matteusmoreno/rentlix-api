package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class InvalidEmailVerificationTokenException extends RentlixApiException {

    public InvalidEmailVerificationTokenException(Features feature) {
        super(
                Errors.EMAIL_VERIFICATION_TOKEN_INVALID.name(),
                Errors.EMAIL_VERIFICATION_TOKEN_INVALID.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_BAD_REQUEST,
                true
        );
    }
}
