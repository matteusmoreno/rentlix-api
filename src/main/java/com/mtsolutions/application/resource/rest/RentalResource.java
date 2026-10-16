package com.mtsolutions.application.resource.rest;

import com.mtsolutions.application.common.RequestParam;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.resource.rest.examples.RentalResourceExamples;
import com.mtsolutions.domain.controller.RentalController;
import com.mtsolutions.domain.dto.request.CreateRentalRequestDto;
import com.mtsolutions.domain.dto.response.RentalPageResponseDto;
import com.mtsolutions.domain.dto.response.RentalResponseDto;
import com.mtsolutions.domain.entity.Rental;
import com.mtsolutions.domain.model.RentalPage;
import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
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
@Path("/v1/rentals")
@Tag(name = "Rental", description = "Endpoints for managing rentals")
public class RentalResource {

    private final RentalController rentalController;

    public RentalResource(RentalController rentalController) {
        this.rentalController = rentalController;
    }

    @POST
    @Path("/create")
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    @Operation(
            summary = "Create rental",
            description = "Creates an active rental for an available asset of an active company and marks the asset as rented. The price and the price period are copied from the asset. The contract URL stays empty until the contract is generated. The lessee must be a lessee of that company. The authenticated user must be an admin of that company."
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = CreateRentalRequestDto.class),
                    examples = @ExampleObject(
                            name = "Create Rental",
                            value = RentalResourceExamples.CREATE_RENTAL_EXAMPLE
                    )
            )
    )
    public Response create(@Valid CreateRentalRequestDto request) throws RentlixApiException {
        Rental rental = this.rentalController.createRental(request);

        return Response.status(Response.Status.CREATED)
                .entity(new RentalResponseDto(rental))
                .build();
    }

    @POST
    @Path("/{rentalId}/finish")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Finish rental",
            description = "Finishes an active rental and marks the asset as available. The authenticated user must be an admin of the company that owns the asset."
    )
    public Response finish(
            @PathParam(RequestParam.RENTAL_ID) @NotNull(message = "rentalId is required") UUID rentalId) throws RentlixApiException {
        Rental rental = this.rentalController.finishRental(rentalId);

        return Response.status(Response.Status.OK)
                .entity(new RentalResponseDto(rental))
                .build();
    }

    @GET
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "List rentals",
            description = "Lists the rentals of a company, newest first. An admin sees every rental of the company. A lessee sees only the rentals where they are the lessee. Page starts at 0. Size defaults to 20 and the maximum is 100."
    )
    public Response list(
            @QueryParam(RequestParam.COMPANY_ID) @NotNull(message = "companyId is required") UUID companyId,
            @QueryParam(RequestParam.PAGE) @DefaultValue("0") int page,
            @QueryParam(RequestParam.SIZE) @DefaultValue("20") int size) throws RentlixApiException {
        RentalPage rentalPage = this.rentalController.listRentals(companyId, page, size);

        return Response.status(Response.Status.OK)
                .entity(new RentalPageResponseDto(rentalPage))
                .build();
    }

    @GET
    @Path("/{rentalId}")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Get rental",
            description = "Returns a rental. An admin of the company that owns the asset can see it. A lessee can see it only when they are the lessee."
    )
    public Response getRental(
            @PathParam(RequestParam.RENTAL_ID) @NotNull(message = "rentalId is required") UUID rentalId) throws RentlixApiException {
        Rental rental = this.rentalController.getRental(rentalId);

        return Response.status(Response.Status.OK)
                .entity(new RentalResponseDto(rental))
                .build();
    }
}
