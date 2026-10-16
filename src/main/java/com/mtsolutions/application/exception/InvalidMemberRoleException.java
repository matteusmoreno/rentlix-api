package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.domain.constant.Errors;
import org.apache.http.HttpStatus;

public class InvalidMemberRoleException extends RentlixApiException {

    public InvalidMemberRoleException(Features feature) {
        super(
                Errors.MEMBER_ROLE_INVALID.name(),
                Errors.MEMBER_ROLE_INVALID.getDescription(),
                feature,
                ErrorOrigins.RENTLIX,
                HttpStatus.SC_BAD_REQUEST,
                true
        );
    }
}
