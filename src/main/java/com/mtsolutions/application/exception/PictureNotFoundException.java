package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class PictureNotFoundException extends RentlixApiException {

    public PictureNotFoundException(Features feature) {
        super(
                Errors.PICTURE_NOT_FOUND.name(),
                Errors.PICTURE_NOT_FOUND.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_NOT_FOUND,
                true
        );
    }
}
