package com.mtsolutions.application.resource.rest;

import com.mtsolutions.application.common.RequestParam;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.resource.rest.examples.AssetResourceExamples;
import com.mtsolutions.domain.controller.AssetController;
import com.mtsolutions.domain.dto.request.CreateAssetRequestDto;
import com.mtsolutions.domain.dto.response.AssetResponseDto;
import com.mtsolutions.domain.entity.Asset;
import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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
import java.util.UUID;

@RequestScoped
@Path("/v1/assets")
@Tag(name = "Asset", description = "Endpoints for managing assets")
public class AssetResource {

    private final AssetController assetController;

    public AssetResource(AssetController assetController) {
        this.assetController = assetController;
    }

    @POST
    @Path("/create")
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    @Operation(
            summary = "Create Asset",
            description = "Creates an available asset for an active company. The authenticated user must be an admin of that company. Motorcycles and cars require vehicle details. Houses and apartments require property details. The main image and the gallery are uploaded after the asset is created."
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = CreateAssetRequestDto.class),
                    examples = @ExampleObject(
                            name = "Create Asset",
                            value = AssetResourceExamples.CREATE_ASSET_EXAMPLE
                    )
            )
    )
    public Response create(@Valid CreateAssetRequestDto request) throws RentlixApiException {
        Asset asset = this.assetController.createAsset(request);

        return Response.status(Response.Status.CREATED)
                .entity(new AssetResponseDto(asset))
                .build();
    }

    @POST
    @Path("/{assetId}/image")
    @Authenticated
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RunOnVirtualThread
    @Operation(
            summary = "Upload asset image",
            description = "Uploads the main image of an asset to Cloudinary and replaces the previous one. The authenticated user must be an admin of the company that owns the asset."
    )
    public Response uploadImage(
            @PathParam(RequestParam.ASSET_ID) @NotNull(message = "assetId is required") UUID assetId,
            @RestForm("image")
            @NotNull(message = "image is required")
            @Schema(type = SchemaType.STRING, format = "binary", description = "Main image", required = true)
            FileUpload image) throws RentlixApiException {
        Asset asset = this.assetController.uploadImage(assetId, this.read(image));

        return Response.status(Response.Status.OK)
                .entity(new AssetResponseDto(asset))
                .build();
    }

    @DELETE
    @Path("/{assetId}/image")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Delete asset image",
            description = "Removes the main image of an asset from Cloudinary. The authenticated user must be an admin of the company that owns the asset."
    )
    public Response deleteImage(
            @PathParam(RequestParam.ASSET_ID) @NotNull(message = "assetId is required") UUID assetId) throws RentlixApiException {
        Asset asset = this.assetController.deleteImage(assetId);

        return Response.status(Response.Status.OK)
                .entity(new AssetResponseDto(asset))
                .build();
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
