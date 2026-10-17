package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class PlanFixedException extends RentlixApiException {

    public PlanFixedException(Features feature) {
        super(
                Errors.PLAN_FIXED.name(),
                Errors.PLAN_FIXED.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_CONFLICT,
                true
        );
    }
}
