package com.mtsolutions.application.resource.rest;

import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.resource.rest.examples.RentalResourceExamples;
import com.mtsolutions.domain.controller.RentalController;
import com.mtsolutions.domain.dto.request.CreateRentalRequestDto;
import com.mtsolutions.domain.dto.response.RentalResponseDto;
import com.mtsolutions.domain.entity.Rental;
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
}
