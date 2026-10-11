package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class CloudinaryUnavailableException extends RentlixApiException {

    public CloudinaryUnavailableException(Features feature) {
        super(
                Errors.CLOUDINARY_UNAVAILABLE.name(),
                Errors.CLOUDINARY_UNAVAILABLE.getDescription(),
                feature,
                ErrorOrigins.CLOUDINARY,
                HttpStatus.SC_BAD_GATEWAY,
                false
        );
    }
}
