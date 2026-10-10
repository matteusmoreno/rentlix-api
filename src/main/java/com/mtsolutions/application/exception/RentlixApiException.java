package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import lombok.Getter;
import org.apache.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;

@Getter
public class RentlixApiException extends Exception {

    private final ExceptionBasicInfo basicInfo;
    private final int httpStatus;
    private final boolean businessError;

    public RentlixApiException(String originCode, String message, Features feature, ErrorOrigins origin, boolean businessError) {
        super(message);
        this.basicInfo = ExceptionBasicInfo.builder()
                .originCode(originCode)
                .feature(feature)
                .message(message)
                .origin(origin)
                .build();
        this.httpStatus = HttpStatus.SC_UNPROCESSABLE_ENTITY;
        this.businessError = businessError;
    }

    public RentlixApiException(String originCode, String message, Features feature, ErrorOrigins origin, int httpStatus, boolean businessError) {
        super(message);
        this.basicInfo = ExceptionBasicInfo.builder()
                .originCode(originCode)
                .feature(feature)
                .message(message)
                .origin(origin)
                .build();
        this.httpStatus = httpStatus;
        this.businessError = businessError;
    }

    public String getStackTraceAsString() {
        StringWriter sw = new StringWriter();
        this.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
