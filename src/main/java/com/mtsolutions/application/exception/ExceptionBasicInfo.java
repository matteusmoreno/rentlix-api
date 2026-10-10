package com.mtsolutions.application.exception;

import com.mtsolutions.application.common.ErrorOrigins;
import com.mtsolutions.application.common.Features;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter @Setter
public class ExceptionBasicInfo implements Serializable {

    private static final long serialVersionUID = 3881065264248149493L;
    private String message;
    private ErrorOrigins origin;
    private String originCode;
    private Features feature;
}
