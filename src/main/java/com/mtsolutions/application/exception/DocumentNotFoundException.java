package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class DocumentNotFoundException extends RentlixApiException {

    public DocumentNotFoundException(Features feature) {
        super(
                Errors.DOCUMENT_NOT_FOUND.name(),
                Errors.DOCUMENT_NOT_FOUND.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_NOT_FOUND,
                true
        );
    }
}
