package com.mtsolutions.application.exception.mapper;

import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.domain.constant.Errors;
import com.mtsolutions.domain.model.ErrorInfo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.http.HttpStatus;

import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Context
    UriInfo uriInfo;

    private final DateComponent dateComponent;

    public ConstraintViolationExceptionMapper(DateComponent dateComponent) {
        this.dateComponent = dateComponent;
    }

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .map(this::violationMessage)
                .collect(Collectors.joining(", "));

        if (message.isBlank()) {
            message = Errors.INVALID_REQUEST.getDescription();
        }

        ErrorInfo errorInfo = ErrorInfo.builder()
                .errorCode(Errors.INVALID_REQUEST.name())
                .message(message)
                .status(HttpStatus.SC_BAD_REQUEST)
                .path(uriInfo.getPath())
                .timestamp(this.dateComponent.now())
                .build();

        return Response.status(HttpStatus.SC_BAD_REQUEST).entity(errorInfo).build();
    }

    private String violationMessage(ConstraintViolation<?> violation) {
        return violation.getMessage();
    }
}
