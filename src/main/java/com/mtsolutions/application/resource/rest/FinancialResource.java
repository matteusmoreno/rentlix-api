package com.mtsolutions.application.resource.rest;

import com.mtsolutions.application.common.RequestParam;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.resource.rest.examples.FinancialResourceExamples;
import com.mtsolutions.domain.controller.FinancialController;
import com.mtsolutions.domain.dto.request.CreateFinancialRequestDto;
import com.mtsolutions.domain.dto.request.UpdateFinancialRequestDto;
import com.mtsolutions.domain.dto.response.FinancialPageResponseDto;
import com.mtsolutions.domain.dto.response.FinancialResponseDto;
import com.mtsolutions.domain.dto.response.FinancialResultResponseDto;
import com.mtsolutions.domain.entity.Financial;
import com.mtsolutions.domain.model.FinancialPage;
import com.mtsolutions.domain.model.FinancialResult;
import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
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

import java.time.LocalDate;
import java.util.UUID;

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
            description = "Records an income or an expense for an active company. Direction says whether money came in or went out. Type says what the entry is, such as rent, deposit or maintenance. The asset and the rental are optional. A rental entry uses the rental asset. The authenticated user must be a lessor of the company."
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

    @GET
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "List financial entries",
            description = "Lists the financial entries of a company, latest date first. The asset and the period are optional. The same day is included on both ends. Page starts at 0. Size defaults to 20 and the maximum is 100. The authenticated user must be a lessor of the company."
    )
    public Response list(
            @QueryParam(RequestParam.COMPANY_ID) @NotNull(message = "companyId is required") UUID companyId,
            @QueryParam(RequestParam.ASSET_ID) UUID assetId,
            @QueryParam(RequestParam.FROM) LocalDate from,
            @QueryParam(RequestParam.TO) LocalDate to,
            @QueryParam(RequestParam.PAGE) @DefaultValue("0") int page,
            @QueryParam(RequestParam.SIZE) @DefaultValue("20") int size) throws RentlixApiException {
        FinancialPage financialPage = this.financialController.listFinancials(companyId, assetId, from, to, page, size);

        return Response.status(Response.Status.OK)
                .entity(new FinancialPageResponseDto(financialPage))
                .build();
    }

    @GET
    @Path("/result")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Get financial result",
            description = "Returns the income, the expense and the result of a company. With an asset, the result is that asset. A positive result is profit. A negative result is loss. The period is optional and includes both days. The authenticated user must be a lessor of the company."
    )
    public Response result(
            @QueryParam(RequestParam.COMPANY_ID) @NotNull(message = "companyId is required") UUID companyId,
            @QueryParam(RequestParam.ASSET_ID) UUID assetId,
            @QueryParam(RequestParam.FROM) LocalDate from,
            @QueryParam(RequestParam.TO) LocalDate to) throws RentlixApiException {
        FinancialResult financialResult = this.financialController.financialResult(companyId, assetId, from, to);

        return Response.status(Response.Status.OK)
                .entity(new FinancialResultResponseDto(financialResult))
                .build();
    }

    @GET
    @Path("/{financialId}")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Get financial entry",
            description = "Returns a financial entry. The authenticated user must be a lessor of the company."
    )
    public Response getFinancial(
            @PathParam(RequestParam.FINANCIAL_ID) @NotNull(message = "financialId is required") UUID financialId) throws RentlixApiException {
        Financial financial = this.financialController.getFinancial(financialId);

        return Response.status(Response.Status.OK)
                .entity(new FinancialResponseDto(financial))
                .build();
    }

    @PUT
    @Path("/{financialId}/update")
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    @Operation(
            summary = "Update financial entry",
            description = "Updates a financial entry. A null field is left unchanged and a blank description is rejected. Company, asset and rental stay unchanged. The authenticated user must be a lessor of the active company."
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = UpdateFinancialRequestDto.class),
                    examples = @ExampleObject(
                            name = "Update Financial",
                            value = FinancialResourceExamples.UPDATE_FINANCIAL_EXAMPLE
                    )
            )
    )
    public Response update(
            @PathParam(RequestParam.FINANCIAL_ID) @NotNull(message = "financialId is required") UUID financialId,
            @Valid UpdateFinancialRequestDto request) throws RentlixApiException {
        Financial financial = this.financialController.updateFinancial(financialId, request);

        return Response.status(Response.Status.OK)
                .entity(new FinancialResponseDto(financial))
                .build();
    }

    @DELETE
    @Path("/{financialId}")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Delete financial entry",
            description = "Removes a financial entry. The result no longer includes it. The authenticated user must be a lessor of the active company."
    )
    public Response delete(
            @PathParam(RequestParam.FINANCIAL_ID) @NotNull(message = "financialId is required") UUID financialId) throws RentlixApiException {
        this.financialController.deleteFinancial(financialId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
