package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class CompanyStatusConflictException extends RentlixApiException {

    public CompanyStatusConflictException(Features feature) {
        super(
                Errors.COMPANY_STATUS_CONFLICT.name(),
                Errors.COMPANY_STATUS_CONFLICT.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_CONFLICT,
                true
        );
    }
}
