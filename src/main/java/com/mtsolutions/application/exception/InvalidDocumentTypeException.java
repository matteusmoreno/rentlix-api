package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class InvalidDocumentTypeException extends RentlixApiException {

    public InvalidDocumentTypeException(Features feature) {
        super(
                Errors.DOCUMENT_TYPE_INVALID.name(),
                Errors.DOCUMENT_TYPE_INVALID.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_BAD_REQUEST,
                true
        );
    }
}
