package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class InvalidPasswordResetCodeException extends RentlixApiException {

    public InvalidPasswordResetCodeException(Features feature) {
        super(
                Errors.PASSWORD_RESET_CODE_INVALID.name(),
                Errors.PASSWORD_RESET_CODE_INVALID.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_BAD_REQUEST,
                true
        );
    }
}
