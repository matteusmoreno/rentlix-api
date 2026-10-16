package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class InvalidRentalPeriodException extends RentlixApiException {

    public InvalidRentalPeriodException(Features feature) {
        super(
                Errors.RENTAL_PERIOD_INVALID.name(),
                Errors.RENTAL_PERIOD_INVALID.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_BAD_REQUEST,
                true
        );
    }
}
