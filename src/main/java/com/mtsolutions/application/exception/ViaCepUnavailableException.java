package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class ViaCepUnavailableException extends RentlixApiException {

    public ViaCepUnavailableException(Features feature) {
        super(
                Errors.VIA_CEP_UNAVAILABLE.name(),
                Errors.VIA_CEP_UNAVAILABLE.getDescription(),
                feature,
                ErrorOrigins.VIA_CEP,
                HttpStatus.SC_BAD_GATEWAY,
                false
        );
    }
}
