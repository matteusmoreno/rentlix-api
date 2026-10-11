package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class AccessDeniedException extends RentlixApiException {

    public AccessDeniedException(Features feature) {
        super(
                Errors.ACCESS_DENIED.name(),
                Errors.ACCESS_DENIED.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_FORBIDDEN,
                true
        );
    }
}
