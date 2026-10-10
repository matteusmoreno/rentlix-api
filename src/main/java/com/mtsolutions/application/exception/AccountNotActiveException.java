package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class AccountNotActiveException extends RentlixApiException {

    public AccountNotActiveException(Features feature) {
        super(
                Errors.ACCOUNT_NOT_ACTIVE.name(),
                Errors.ACCOUNT_NOT_ACTIVE.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_FORBIDDEN,
                true
        );
    }
}
