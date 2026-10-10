package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class ZipCodeNotFoundException extends RentlixApiException {

    public ZipCodeNotFoundException(Features feature) {
        super(
                Errors.ZIP_CODE_NOT_FOUND.name(),
                Errors.ZIP_CODE_NOT_FOUND.getDescription(),
                feature,
                ErrorOrigins.VIA_CEP,
                HttpStatus.SC_NOT_FOUND,
                true
        );
    }
}
