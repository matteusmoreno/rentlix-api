package com.mtsolutions.application.resource.rest;

import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.resource.rest.examples.CompanyResourceExamples;
import com.mtsolutions.domain.controller.CompanyController;
import com.mtsolutions.domain.dto.request.CreateCompanyRequestDto;
import com.mtsolutions.domain.dto.response.CompanyResponseDto;
import com.mtsolutions.domain.entity.Company;
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
@Path("/v1/companies")
@Tag(name = "Company", description = "Endpoints for managing companies")
public class CompanyResource {

    private final CompanyController companyController;

    public CompanyResource(CompanyController companyController) {
        this.companyController = companyController;
    }

    @POST
    @Path("/create")
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    @Operation(
            summary = "Create Company",
            description = "Creates a company for the authenticated user and adds that user as admin. Status starts as ACTIVE."
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = CreateCompanyRequestDto.class),
                    examples = @ExampleObject(
                            name = "Create Company",
                            value = CompanyResourceExamples.CREATE_COMPANY_EXAMPLE
                    )
            )
    )
    public Response create(@Valid CreateCompanyRequestDto request) throws RentlixApiException {
        Company company = this.companyController.createCompany(request);

        return Response.status(Response.Status.CREATED)
                .entity(new CompanyResponseDto(company))
                .build();
    }
}
