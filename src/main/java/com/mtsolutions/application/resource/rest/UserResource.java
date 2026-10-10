package com.mtsolutions.application.resource.rest;

import com.mtsolutions.application.common.RequestParam;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.resource.rest.examples.UserResourceExamples;
import com.mtsolutions.domain.controller.UserController;
import com.mtsolutions.domain.dto.request.CreateUserRequestDto;
import com.mtsolutions.domain.dto.response.UserResponseDto;
import com.mtsolutions.domain.entity.User;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@RequestScoped
@Path("/v1/users")
@Tag(name = "User", description = "Endpoints for managing users")
public class UserResource {

    private final UserController userController;

    public UserResource(UserController userController) {
        this.userController = userController;
    }

    @POST
    @Path("/create")
    @RunOnVirtualThread
    @Operation(
            summary = "Create User",
            description = "Creates a new user"
    )
    @RequestBody(
            description = "User creation request",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = CreateUserRequestDto.class),
                    examples = {
                            @ExampleObject(
                                    name = "Example request",
                                    summary = "Create User Example",
                                    value = UserResourceExamples.CREATE_NEW_USER_EXAMPLE
                            )
                    }
            )
    )
    public Response create(@Valid CreateUserRequestDto request) throws RentlixApiException {
        User user = this.userController.createUser(request);

        return  Response.status(Response.Status.CREATED)
                .entity(new UserResponseDto(user))
                .build();
    }

    @GET
    @Path("/{userId}")
    @RunOnVirtualThread
    @Operation(
            summary = "Get User by ID",
            description = "Retrieves a user by their unique ID"
    )
    public Response getUserById(@PathParam(RequestParam.USER_ID) UUID userId) throws RentlixApiException {
        User user = this.userController.getUserById(userId);

        return Response.status(Response.Status.OK)
                .entity(new UserResponseDto(user))
                .build();
    }

    @POST
    @Path("/{userId}/email-verification")
    @RunOnVirtualThread
    @Operation(
            summary = "Send email verification",
            description = "Generates a verification token for the user. Email delivery will be implemented in EmailService."
    )
    public Response sendEmailVerification(@PathParam(RequestParam.USER_ID) UUID userId) throws RentlixApiException {
        this.userController.sendEmailVerification(userId);

        return Response.status(Response.Status.NO_CONTENT)
                .build();
    }

    @POST
    @Path("/verify-email")
    @RunOnVirtualThread
    @Operation(
            summary = "Verify email",
            description = "Confirms the account using the verification token"
    )
    public Response verifyEmail(@QueryParam(RequestParam.TOKEN) @NotBlank(message = "token is required") String token) throws RentlixApiException {
        this.userController.verifyEmail(token);

        return Response.status(Response.Status.NO_CONTENT)
                .build();
    }
}
