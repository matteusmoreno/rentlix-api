package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class ExpiredPasswordResetCodeException extends RentlixApiException {

    public ExpiredPasswordResetCodeException(Features feature) {
        super(
                Errors.PASSWORD_RESET_CODE_EXPIRED.name(),
                Errors.PASSWORD_RESET_CODE_EXPIRED.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_GONE,
                true
        );
    }
}
