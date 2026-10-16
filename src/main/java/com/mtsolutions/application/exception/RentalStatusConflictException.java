package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class RentalStatusConflictException extends RentlixApiException {

    public RentalStatusConflictException(Features feature) {
        super(
                Errors.RENTAL_STATUS_CONFLICT.name(),
                Errors.RENTAL_STATUS_CONFLICT.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_CONFLICT,
                true
        );
    }
}
