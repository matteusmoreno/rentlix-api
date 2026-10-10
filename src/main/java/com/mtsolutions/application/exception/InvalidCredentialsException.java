package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class InvalidCredentialsException extends RentlixApiException {

    public InvalidCredentialsException(Features feature) {
        super(
                Errors.INVALID_CREDENTIALS.name(),
                Errors.INVALID_CREDENTIALS.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_UNAUTHORIZED,
                true
        );
    }
}
