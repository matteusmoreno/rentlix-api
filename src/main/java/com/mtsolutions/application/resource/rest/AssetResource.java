package com.mtsolutions.application.resource.rest;

import com.mtsolutions.application.common.RequestParam;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.resource.rest.examples.AssetResourceExamples;
import com.mtsolutions.domain.controller.AssetController;
import com.mtsolutions.domain.dto.request.CreateAssetRequestDto;
import com.mtsolutions.domain.dto.request.UpdateAssetRequestDto;
import com.mtsolutions.domain.dto.response.AssetPageResponseDto;
import com.mtsolutions.domain.dto.response.AssetResponseDto;
import com.mtsolutions.domain.dto.response.PublicAssetPageResponseDto;
import com.mtsolutions.domain.dto.response.PublicAssetResponseDto;
import com.mtsolutions.domain.entity.Asset;
import com.mtsolutions.domain.model.AssetPage;
import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
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
            description = "Creates an available asset for an active company. The authenticated user must be a lessor of that company. Motorcycles and cars require vehicle details. Houses and apartments require property details. The main image and the gallery are uploaded after the asset is created."
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

    @GET
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "List assets",
            description = "Lists the assets of a company, newest first. The authenticated user must be a member of that company. Page starts at 0. Size defaults to 20 and the maximum is 100."
    )
    public Response list(
            @QueryParam(RequestParam.COMPANY_ID) @NotNull(message = "companyId is required") UUID companyId,
            @QueryParam(RequestParam.PAGE) @DefaultValue("0") int page,
            @QueryParam(RequestParam.SIZE) @DefaultValue("20") int size) throws RentlixApiException {
        AssetPage assetPage = this.assetController.listAssets(companyId, page, size);

        return Response.status(Response.Status.OK)
                .entity(new AssetPageResponseDto(assetPage))
                .build();
    }

    @GET
    @Path("/public")
    @PermitAll
    @RunOnVirtualThread
    @Operation(
            summary = "List public assets",
            description = "Lists available assets from active companies, newest first. Plate, renavam, chassis, property registration and the full address are omitted. Page starts at 0. Size defaults to 20 and the maximum is 100."
    )
    public Response listPublic(
            @QueryParam(RequestParam.PAGE) @DefaultValue("0") int page,
            @QueryParam(RequestParam.SIZE) @DefaultValue("20") int size) {
        AssetPage assetPage = this.assetController.listPublicAssets(page, size);

        return Response.status(Response.Status.OK)
                .entity(new PublicAssetPageResponseDto(assetPage))
                .build();
    }

    @GET
    @Path("/{assetId}")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Get asset",
            description = "Returns an asset with its vehicle or property details, main image and gallery. The authenticated user must be a member of the company that owns the asset."
    )
    public Response getAsset(
            @PathParam(RequestParam.ASSET_ID) @NotNull(message = "assetId is required") UUID assetId) throws RentlixApiException {
        Asset asset = this.assetController.getAsset(assetId);

        return Response.status(Response.Status.OK)
                .entity(new AssetResponseDto(asset))
                .build();
    }

    @PUT
    @Path("/{assetId}/update")
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    @Operation(
            summary = "Update asset",
            description = "Updates the asset information. A null field is left unchanged and a blank value is rejected. Type, company, images and status stay unchanged. The authenticated user must be a lessor of the company that owns the asset."
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = UpdateAssetRequestDto.class),
                    examples = @ExampleObject(
                            name = "Update Asset",
                            value = AssetResourceExamples.UPDATE_ASSET_EXAMPLE
                    )
            )
    )
    public Response update(
            @Valid UpdateAssetRequestDto request,
            @PathParam(RequestParam.ASSET_ID) @NotNull(message = "assetId is required") UUID assetId) throws RentlixApiException {
        Asset asset = this.assetController.updateAsset(request, assetId);

        return Response.status(Response.Status.OK)
                .entity(new AssetResponseDto(asset))
                .build();
    }

    @GET
    @Path("/{assetId}/public")
    @PermitAll
    @RunOnVirtualThread
    @Operation(
            summary = "Get public asset",
            description = "Returns the public listing of an available asset from an active company. Plate, renavam, chassis, property registration and the full address stay on the company endpoint."
    )
    public Response getPublicAsset(
            @PathParam(RequestParam.ASSET_ID) @NotNull(message = "assetId is required") UUID assetId) throws RentlixApiException {
        Asset asset = this.assetController.getPublicAsset(assetId);

        return Response.status(Response.Status.OK)
                .entity(new PublicAssetResponseDto(asset))
                .build();
    }

    @POST
    @Path("/{assetId}/image")
    @Authenticated
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RunOnVirtualThread
    @Operation(
            summary = "Upload asset image",
            description = "Uploads the main image of an asset to Cloudinary and replaces the previous one. The authenticated user must be a lessor of the company that owns the asset."
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
            description = "Removes the main image of an asset from Cloudinary. The authenticated user must be a lessor of the company that owns the asset."
    )
    public Response deleteImage(
            @PathParam(RequestParam.ASSET_ID) @NotNull(message = "assetId is required") UUID assetId) throws RentlixApiException {
        Asset asset = this.assetController.deleteImage(assetId);

        return Response.status(Response.Status.OK)
                .entity(new AssetResponseDto(asset))
                .build();
    }

    @POST
    @Path("/{assetId}/gallery")
    @Authenticated
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RunOnVirtualThread
    @Operation(
            summary = "Upload gallery image",
            description = "Uploads one image to the end of the asset gallery. Position 0 is the first image. The authenticated user must be a lessor of the company that owns the asset."
    )
    public Response uploadGalleryImage(
            @PathParam(RequestParam.ASSET_ID) @NotNull(message = "assetId is required") UUID assetId,
            @RestForm("image")
            @NotNull(message = "image is required")
            @Schema(type = SchemaType.STRING, format = "binary", description = "Gallery image", required = true)
            FileUpload image) throws RentlixApiException {
        Asset asset = this.assetController.uploadGalleryImage(assetId, this.read(image));

        return Response.status(Response.Status.CREATED)
                .entity(new AssetResponseDto(asset))
                .build();
    }

    @DELETE
    @Path("/{assetId}/gallery/{position}")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Delete gallery image",
            description = "Removes one gallery image by its position. Position 0 is the first image. The following images move up. The authenticated user must be a lessor of the company that owns the asset."
    )
    public Response deleteGalleryImage(
            @PathParam(RequestParam.ASSET_ID) @NotNull(message = "assetId is required") UUID assetId,
            @PathParam(RequestParam.POSITION) @NotNull(message = "position is required") Integer position) throws RentlixApiException {
        Asset asset = this.assetController.deleteGalleryImage(assetId, position);

        return Response.status(Response.Status.OK)
                .entity(new AssetResponseDto(asset))
                .build();
    }

    @POST
    @Path("/{assetId}/rent")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Rent asset",
            description = "Marks an available asset as rented. The authenticated user must be a lessor of the company that owns the asset."
    )
    public Response rent(
            @PathParam(RequestParam.ASSET_ID) @NotNull(message = "assetId is required") UUID assetId) throws RentlixApiException {
        Asset asset = this.assetController.rentAsset(assetId);

        return Response.status(Response.Status.OK)
                .entity(new AssetResponseDto(asset))
                .build();
    }

    @POST
    @Path("/{assetId}/maintenance")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Start asset maintenance",
            description = "Marks an available asset as under maintenance. The authenticated user must be a lessor of the company that owns the asset."
    )
    public Response startMaintenance(
            @PathParam(RequestParam.ASSET_ID) @NotNull(message = "assetId is required") UUID assetId) throws RentlixApiException {
        Asset asset = this.assetController.startMaintenance(assetId);

        return Response.status(Response.Status.OK)
                .entity(new AssetResponseDto(asset))
                .build();
    }

    @POST
    @Path("/{assetId}/inactivate")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Inactivate asset",
            description = "Marks an available asset as inactive. The authenticated user must be a lessor of the company that owns the asset."
    )
    public Response inactivate(
            @PathParam(RequestParam.ASSET_ID) @NotNull(message = "assetId is required") UUID assetId) throws RentlixApiException {
        Asset asset = this.assetController.inactivateAsset(assetId);

        return Response.status(Response.Status.OK)
                .entity(new AssetResponseDto(asset))
                .build();
    }

    @POST
    @Path("/{assetId}/activate")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Activate asset",
            description = "Marks a maintenance or inactive asset as available. A rented asset with an active rental must be finished first. The authenticated user must be a lessor of the company that owns the asset."
    )
    public Response activate(
            @PathParam(RequestParam.ASSET_ID) @NotNull(message = "assetId is required") UUID assetId) throws RentlixApiException {
        Asset asset = this.assetController.activateAsset(assetId);

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
