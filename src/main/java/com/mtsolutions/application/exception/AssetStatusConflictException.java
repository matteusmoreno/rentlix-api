package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class AssetStatusConflictException extends RentlixApiException {

    public AssetStatusConflictException(Features feature) {
        super(
                Errors.ASSET_STATUS_CONFLICT.name(),
                Errors.ASSET_STATUS_CONFLICT.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_CONFLICT,
                true
        );
    }
}
