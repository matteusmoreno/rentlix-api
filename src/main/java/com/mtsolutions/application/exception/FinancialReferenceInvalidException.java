package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class FinancialReferenceInvalidException extends RentlixApiException {

    public FinancialReferenceInvalidException(Features feature) {
        super(
                Errors.FINANCIAL_REFERENCE_INVALID.name(),
                Errors.FINANCIAL_REFERENCE_INVALID.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_BAD_REQUEST,
                true
        );
    }
}
