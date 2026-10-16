package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class InvalidAssetDetailsException extends RentlixApiException {

    public InvalidAssetDetailsException(Features feature) {
        super(
                Errors.ASSET_DETAILS_INVALID.name(),
                Errors.ASSET_DETAILS_INVALID.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_BAD_REQUEST,
                true
        );
    }
}
