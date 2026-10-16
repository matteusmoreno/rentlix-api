package com.mtsolutions.application.resource.rest;

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
import jakarta.ws.rs.Consumes;
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
}
