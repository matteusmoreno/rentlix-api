package com.mtsolutions.application.exception.mapper;

import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.application.exception.ExceptionBasicInfo;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.model.ErrorInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


@Provider
public class RentlixApiExceptionMapper implements ExceptionMapper<RentlixApiException> {

    @Context
    UriInfo uriInfo;

    private final DateComponent dateComponent;

    public RentlixApiExceptionMapper(DateComponent dateComponent) {
        this.dateComponent = dateComponent;
    }

    @Override
    public Response toResponse(RentlixApiException exception) {

        ExceptionBasicInfo basicInfo = exception.getBasicInfo();

        ErrorInfo errorInfo = ErrorInfo.builder()
                .errorCode(basicInfo.getOriginCode())
                .message(basicInfo.getMessage())
                .status(exception.getHttpStatus())
                .path(uriInfo.getPath())
                .timestamp(this.dateComponent.now())
                .build();

        return Response.status(exception.getHttpStatus()).entity(errorInfo).build();
    }
}
