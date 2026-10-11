package com.mtsolutions.application.resource.rest;

import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.resource.rest.examples.AuthExamples;
import com.mtsolutions.domain.controller.AuthController;
import com.mtsolutions.domain.dto.request.LoginRequestDto;
import com.mtsolutions.domain.dto.response.LoginResponseDto;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@RequestScoped
@Path("/v1/auth")
@Tag(name = "Auth", description = "Endpoints for authentication")
public class AuthResource {

    private final AuthController authController;

    public AuthResource(AuthController authController) {
        this.authController = authController;
    }

    @POST
    @Path("/login")
    @PermitAll
    @Operation(
            summary = "Login",
            description = "Authenticate a user and return a token"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = LoginRequestDto.class),
                    examples = @ExampleObject(
                            name = "Login",
                            value = AuthExamples.LOGIN_REQUEST_EXAMPLE
                    )
            )
    )
    public Response login(@Valid LoginRequestDto request) throws RentlixApiException {
        LoginResponseDto response = authController.login(request);

        return  Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }
}
