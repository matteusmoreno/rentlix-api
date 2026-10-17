package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class ContractTemplateInvalidException extends RentlixApiException {

    public ContractTemplateInvalidException(Features feature) {
        super(
                Errors.CONTRACT_TEMPLATE_INVALID.name(),
                Errors.CONTRACT_TEMPLATE_INVALID.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_BAD_REQUEST,
                true
        );
    }
}