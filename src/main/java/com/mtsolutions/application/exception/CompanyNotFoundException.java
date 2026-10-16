package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class CompanyNotFoundException extends RentlixApiException {

    public CompanyNotFoundException(Features feature) {
        super(
                Errors.COMPANY_NOT_FOUND.name(),
                Errors.COMPANY_NOT_FOUND.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_NOT_FOUND,
                true
        );
    }
}
