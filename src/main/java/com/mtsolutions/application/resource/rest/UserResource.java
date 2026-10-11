package com.mtsolutions.application.resource.rest;

import com.mtsolutions.application.common.RequestParam;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.resource.rest.examples.UserResourceExamples;
import com.mtsolutions.domain.controller.UserController;
import com.mtsolutions.domain.dto.request.CreateUserRequestDto;
import com.mtsolutions.domain.dto.response.UserPageResponseDto;
import com.mtsolutions.domain.dto.response.UserResponseDto;
import com.mtsolutions.domain.entity.User;
import com.mtsolutions.domain.model.UserPage;
import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
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
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    @Operation(
            summary = "Create User",
            description = "Creates a new user. Profile picture and documents are uploaded after login."
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = CreateUserRequestDto.class),
                    examples = @ExampleObject(
                            name = "Create User",
                            value = UserResourceExamples.CREATE_NEW_USER_EXAMPLE
                    )
            )
    )
    public Response create(@Valid CreateUserRequestDto request) throws RentlixApiException {
        User user = this.userController.createUser(request);

        return Response.status(Response.Status.CREATED)
                .entity(new UserResponseDto(user))
                .build();
    }

    @GET
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "List users",
            description = "Lists users with pagination. Page starts at 0. Size defaults to 20 and the maximum is 100."
    )
    public Response listUsers(@QueryParam(RequestParam.PAGE) @DefaultValue("0") int page,
                              @QueryParam(RequestParam.SIZE) @DefaultValue("20") int size) {
        UserPage userPage = this.userController.listUsers(page, size);

        return Response.status(Response.Status.OK)
                .entity(new UserPageResponseDto(userPage))
                .build();
    }

    @GET
    @Path("/{userId}")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Get User by ID",
            description = "Retrieves the authenticated user"
    )
    public Response getUserById(@PathParam(RequestParam.USER_ID) UUID userId) throws RentlixApiException {
        User user = this.userController.getUserById(userId);

        return Response.status(Response.Status.OK)
                .entity(new UserResponseDto(user))
                .build();
    }

    @POST
    @Path("/{userId}/picture")
    @Authenticated
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RunOnVirtualThread
    @Operation(
            summary = "Upload profile picture",
            description = "Uploads the user profile picture to Cloudinary and replaces the previous one"
    )
    public Response uploadPicture(
            @PathParam(RequestParam.USER_ID) UUID userId,
            @RestForm("picture")
            @NotNull(message = "picture is required")
            @Schema(type = SchemaType.STRING, format = "binary", description = "Profile picture", required = true)
            FileUpload picture) throws RentlixApiException {
        User user = this.userController.uploadPicture(userId, this.read(picture));

        return Response.status(Response.Status.OK)
                .entity(new UserResponseDto(user))
                .build();
    }

    @DELETE
    @Path("/{userId}/picture")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Delete profile picture",
            description = "Removes the user profile picture from Cloudinary"
    )
    public Response deletePicture(@PathParam(RequestParam.USER_ID) UUID userId) throws RentlixApiException {
        User user = this.userController.deletePicture(userId);

        return Response.status(Response.Status.OK)
                .entity(new UserResponseDto(user))
                .build();
    }

    @POST
    @Path("/{userId}/documents")
    @Authenticated
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RunOnVirtualThread
    @Operation(
            summary = "Upload user documents",
            description = "Uploads one or more user documents to Cloudinary"
    )
    public Response uploadDocuments(
            @PathParam(RequestParam.USER_ID) UUID userId,
            @RestForm("cnh")
            @Schema(type = SchemaType.STRING, format = "binary", description = "CNH file")
            FileUpload cnh,
            @RestForm("cpf")
            @Schema(type = SchemaType.STRING, format = "binary", description = "CPF file")
            FileUpload cpf,
            @RestForm("rg")
            @Schema(type = SchemaType.STRING, format = "binary", description = "RG file")
            FileUpload rg,
            @RestForm("proofOfResidence")
            @Schema(type = SchemaType.STRING, format = "binary", description = "Proof of residence file")
            FileUpload proofOfResidence,
            @RestForm("criminalRecord")
            @Schema(type = SchemaType.STRING, format = "binary", description = "Criminal record file")
            FileUpload criminalRecord,
            @RestForm("passport")
            @Schema(type = SchemaType.STRING, format = "binary", description = "Passport file")
            FileUpload passport) throws RentlixApiException {
        Map<String, byte[]> documents = new LinkedHashMap<>();
        this.addDocument(documents, "cnh", cnh);
        this.addDocument(documents, "cpf", cpf);
        this.addDocument(documents, "rg", rg);
        this.addDocument(documents, "proofofresidence", proofOfResidence);
        this.addDocument(documents, "criminalrecord", criminalRecord);
        this.addDocument(documents, "passport", passport);

        User user = this.userController.uploadDocuments(userId, documents);

        return Response.status(Response.Status.OK)
                .entity(new UserResponseDto(user))
                .build();
    }

    @DELETE
    @Path("/{userId}/documents/{documentType}")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Delete user document",
            description = "Removes one user document from Cloudinary. documentType: cnh, cpf, rg, proofOfResidence, criminalRecord or passport."
    )
    public Response deleteDocument(
            @PathParam(RequestParam.USER_ID) UUID userId,
            @PathParam("documentType") @NotBlank(message = "documentType is required") String documentType) throws RentlixApiException {
        User user = this.userController.deleteDocument(userId, documentType);

        return Response.status(Response.Status.OK)
                .entity(new UserResponseDto(user))
                .build();
    }

    @POST
    @Path("/{userId}/email-verification")
    @PermitAll
    @RunOnVirtualThread
    @Operation(
            summary = "Send email verification",
            description = "Sends a new verification code to the user email"
    )
    public Response sendEmailVerification(@PathParam(RequestParam.USER_ID) UUID userId) throws RentlixApiException {
        this.userController.sendEmailVerification(userId);

        return Response.status(Response.Status.NO_CONTENT)
                .build();
    }

    @POST
    @Path("/verify-email")
    @PermitAll
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

    private void addDocument(Map<String, byte[]> documents, String type, FileUpload file) {
        byte[] content = this.read(file);
        if (content != null && content.length > 0) {
            documents.put(type, content);
        }
    }

    private byte[] read(FileUpload file) {
        if (file == null || file.uploadedFile() == null) {
            return null;
        }
        try {
            return Files.readAllBytes(file.uploadedFile());
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
