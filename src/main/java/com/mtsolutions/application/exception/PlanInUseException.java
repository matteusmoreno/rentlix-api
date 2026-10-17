package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class PlanInUseException extends RentlixApiException {

    public PlanInUseException(Features feature) {
        super(
                Errors.PLAN_IN_USE.name(),
                Errors.PLAN_IN_USE.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_CONFLICT,
                true
        );
    }
}
