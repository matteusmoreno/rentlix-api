package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class CompanyMemberAlreadyExistsException extends RentlixApiException {

    public CompanyMemberAlreadyExistsException(Features feature) {
        super(
                Errors.COMPANY_MEMBER_ALREADY_EXISTS.name(),
                Errors.COMPANY_MEMBER_ALREADY_EXISTS.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_CONFLICT,
                true
        );
    }
}
