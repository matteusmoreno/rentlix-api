package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class ExpiredEmailVerificationTokenException extends RentlixApiException {

    public ExpiredEmailVerificationTokenException(Features feature) {
        super(
                Errors.EMAIL_VERIFICATION_TOKEN_EXPIRED.name(),
                Errors.EMAIL_VERIFICATION_TOKEN_EXPIRED.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_GONE,
                true
        );
    }
}
