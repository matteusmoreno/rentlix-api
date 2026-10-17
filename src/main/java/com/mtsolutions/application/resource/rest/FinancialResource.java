package com.mtsolutions.application.resource.rest;

import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.resource.rest.examples.FinancialResourceExamples;
import com.mtsolutions.domain.controller.FinancialController;
import com.mtsolutions.domain.dto.request.CreateFinancialRequestDto;
import com.mtsolutions.domain.dto.response.FinancialResponseDto;
import com.mtsolutions.domain.entity.Financial;
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
@Path("/v1/financial")
@Tag(name = "Financial", description = "Endpoints for managing financial entries")
public class FinancialResource {

    private final FinancialController financialController;

    public FinancialResource(FinancialController financialController) {
        this.financialController = financialController;
    }

    @POST
    @Path("/create")
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    @Operation(
            summary = "Create financial entry",
            description = "Records an income or an expense for an active company. The asset and the rental are optional. A rental entry uses the rental asset. The authenticated user must be an admin of the company."
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = CreateFinancialRequestDto.class),
                    examples = @ExampleObject(
                            name = "Create Financial",
                            value = FinancialResourceExamples.CREATE_FINANCIAL_EXAMPLE
                    )
            )
    )
    public Response create(@Valid CreateFinancialRequestDto request) throws RentlixApiException {
        Financial financial = this.financialController.createFinancial(request);

        return Response.status(Response.Status.CREATED)
                .entity(new FinancialResponseDto(financial))
                .build();
    }
}
