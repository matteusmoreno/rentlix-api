package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class InvalidLesseeException extends RentlixApiException {

    public InvalidLesseeException(Features feature) {
        super(
                Errors.LESSEE_INVALID.name(),
                Errors.LESSEE_INVALID.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_BAD_REQUEST,
                true
        );
    }
}
