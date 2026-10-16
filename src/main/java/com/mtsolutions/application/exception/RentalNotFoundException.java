package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class RentalNotFoundException extends RentlixApiException {

    public RentalNotFoundException(Features feature) {
        super(
                Errors.RENTAL_NOT_FOUND.name(),
                Errors.RENTAL_NOT_FOUND.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_NOT_FOUND,
                true
        );
    }
}
