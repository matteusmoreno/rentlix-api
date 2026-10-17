package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class PlanAlreadyExistsException extends RentlixApiException {

    public PlanAlreadyExistsException(Features feature) {
        super(
                Errors.PLAN_ALREADY_EXISTS.name(),
                Errors.PLAN_ALREADY_EXISTS.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_CONFLICT,
                true
        );
    }
}
