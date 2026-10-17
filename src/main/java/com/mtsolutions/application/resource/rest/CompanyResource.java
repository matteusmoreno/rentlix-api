package com.mtsolutions.application.resource.rest;

import com.mtsolutions.application.common.RequestParam;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.resource.rest.examples.CompanyResourceExamples;
import com.mtsolutions.domain.controller.CompanyController;
import com.mtsolutions.domain.constant.AssetCategory;
import com.mtsolutions.domain.dto.request.AddCompanyMemberRequestDto;
import com.mtsolutions.domain.dto.request.CreateCompanyRequestDto;
import com.mtsolutions.domain.dto.request.SaveContractTemplateRequestDto;
import com.mtsolutions.domain.dto.response.CompanyMemberResponseDto;
import com.mtsolutions.domain.dto.response.CompanyResponseDto;
import com.mtsolutions.domain.dto.response.CompanyUserResponseDto;
import com.mtsolutions.domain.dto.response.ContractTemplateResponseDto;
import com.mtsolutions.domain.dto.response.PublicCompanyResponseDto;
import com.mtsolutions.domain.entity.Company;
import com.mtsolutions.domain.entity.CompanyMember;
import com.mtsolutions.domain.entity.ContractTemplate;
import com.mtsolutions.domain.model.PublicCompany;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
import java.util.List;
import java.util.UUID;

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

    @GET
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "List companies",
            description = "Lists the companies of the authenticated user and the role of that user in each company."
    )
    public Response list() throws RentlixApiException {
        List<CompanyMember> members = this.companyController.listCompanies();
        List<CompanyMemberResponseDto> companies = members.stream()
                .map(CompanyMemberResponseDto::new)
                .toList();

        return Response.status(Response.Status.OK)
                .entity(companies)
                .build();
    }

    @POST
    @Path("/{companyId}/members")
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    @Operation(
            summary = "Add company member",
            description = "Adds a lessor or lessee to the company. The authenticated user must be an admin of that company."
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = AddCompanyMemberRequestDto.class),
                    examples = @ExampleObject(
                            name = "Add Company Member",
                            value = CompanyResourceExamples.ADD_COMPANY_MEMBER_EXAMPLE
                    )
            )
    )
    public Response addMember(@PathParam(RequestParam.COMPANY_ID) @NotNull(message = "companyId is required") UUID companyId,
                              @Valid AddCompanyMemberRequestDto request) throws RentlixApiException {
        CompanyMember member = this.companyController.addMember(companyId, request);

        return Response.status(Response.Status.CREATED)
                .entity(new CompanyMemberResponseDto(member))
                .build();
    }

    @GET
    @Path("/{companyId}/members")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "List company members",
            description = "Lists the members of a company. The authenticated user must belong to that company."
    )
    public Response listMembers(@PathParam(RequestParam.COMPANY_ID) @NotNull(message = "companyId is required") UUID companyId) throws RentlixApiException {
        List<CompanyMember> members = this.companyController.listMembers(companyId);
        List<CompanyUserResponseDto> response = members.stream()
                .map(CompanyUserResponseDto::new)
                .toList();

        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @POST
    @Path("/{companyId}/suspend")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Suspend company",
            description = "Suspends an active company. The authenticated user must be an admin of that company."
    )
    public Response suspend(@PathParam(RequestParam.COMPANY_ID) @NotNull(message = "companyId is required") UUID companyId) throws RentlixApiException {
        Company company = this.companyController.suspendCompany(companyId);

        return Response.status(Response.Status.OK)
                .entity(new CompanyResponseDto(company))
                .build();
    }

    @POST
    @Path("/{companyId}/cancel")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Cancel company",
            description = "Cancels an active or suspended company. A canceled company cannot change status again. The authenticated user must be an admin of that company."
    )
    public Response cancel(@PathParam(RequestParam.COMPANY_ID) @NotNull(message = "companyId is required") UUID companyId) throws RentlixApiException {
        Company company = this.companyController.cancelCompany(companyId);

        return Response.status(Response.Status.OK)
                .entity(new CompanyResponseDto(company))
                .build();
    }

    @POST
    @Path("/{companyId}/reactivate")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Reactivate company",
            description = "Reactivates a suspended company. A canceled company cannot be reactivated. The authenticated user must be an admin of that company."
    )
    public Response reactivate(@PathParam(RequestParam.COMPANY_ID) @NotNull(message = "companyId is required") UUID companyId) throws RentlixApiException {
        Company company = this.companyController.reactivateCompany(companyId);

        return Response.status(Response.Status.OK)
                .entity(new CompanyResponseDto(company))
                .build();
    }

    @PUT
    @Path("/{companyId}/contract-templates/{category}")
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    @Operation(
            summary = "Save contract template",
            description = "Saves the contract template of an active company for vehicles or properties. The content is the editor HTML. Placeholders: {{company.name}}, {{company.cnpj}}, {{lessee.name}}, {{lessee.cpf}}, {{lessee.rg}}, {{lessee.email}}, {{lessee.phone}}, {{lessee.birthDate}}, {{lessee.occupation}}, {{lessee.maritalStatus}}, {{lessee.zipCode}}, {{lessee.street}}, {{lessee.number}}, {{lessee.complement}}, {{lessee.neighborhood}}, {{lessee.city}}, {{lessee.state}}, {{asset.name}}, {{asset.description}}, {{asset.type}}, {{asset.price}}, {{asset.pricePeriod}}, {{vehicle.brand}}, {{vehicle.model}}, {{vehicle.manufactureYear}}, {{vehicle.modelYear}}, {{vehicle.color}}, {{vehicle.plate}}, {{vehicle.renavam}}, {{vehicle.chassis}}, {{vehicle.engineCc}}, {{vehicle.mileage}}, {{property.zipCode}}, {{property.street}}, {{property.number}}, {{property.complement}}, {{property.neighborhood}}, {{property.city}}, {{property.state}}, {{property.totalArea}}, {{property.bedrooms}}, {{property.suites}}, {{property.bathrooms}}, {{property.parkingSpaces}}, {{property.furnished}}, {{property.petFriendly}}, {{property.hasElevator}}, {{property.hasPool}}, {{property.hasGym}}, {{property.hasBarbecue}}, {{property.condoFee}}, {{property.iptu}}, {{property.registrationNumber}}, {{rental.startDate}}, {{rental.endDate}}, {{rental.price}}, {{rental.pricePeriod}}. The authenticated user must be an admin of the company."
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SaveContractTemplateRequestDto.class),
                    examples = @ExampleObject(
                            name = "Save Contract Template",
                            value = CompanyResourceExamples.SAVE_CONTRACT_TEMPLATE_EXAMPLE
                    )
            )
    )
    public Response saveContractTemplate(
            @PathParam(RequestParam.COMPANY_ID) @NotNull(message = "companyId is required") UUID companyId,
            @PathParam(RequestParam.CATEGORY) @NotNull(message = "category is required") AssetCategory category,
            @Valid SaveContractTemplateRequestDto request) throws RentlixApiException {
        ContractTemplate template = this.companyController.saveContractTemplate(companyId, category, request);

        return Response.status(Response.Status.OK)
                .entity(new ContractTemplateResponseDto(template))
                .build();
    }

    @GET
    @Path("/{companyId}/contract-templates/{category}")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Get contract template",
            description = "Returns the contract template of a company for vehicles or properties. The authenticated user must be an admin of the company."
    )
    public Response getContractTemplate(
            @PathParam(RequestParam.COMPANY_ID) @NotNull(message = "companyId is required") UUID companyId,
            @PathParam(RequestParam.CATEGORY) @NotNull(message = "category is required") AssetCategory category) throws RentlixApiException {
        ContractTemplate template = this.companyController.getContractTemplate(companyId, category);

        return Response.status(Response.Status.OK)
                .entity(new ContractTemplateResponseDto(template))
                .build();
    }

    @POST
    @Path("/{companyId}/logo")
    @Authenticated
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RunOnVirtualThread
    @Operation(
            summary = "Upload company logo",
            description = "Uploads the logo of an active company and replaces the previous one. The authenticated user must be an admin of the company."
    )
    public Response uploadLogo(
            @PathParam(RequestParam.COMPANY_ID) @NotNull(message = "companyId is required") UUID companyId,
            @RestForm("logo")
            @NotNull(message = "logo is required")
            @Schema(type = SchemaType.STRING, format = "binary", description = "Company logo", required = true)
            FileUpload logo) throws RentlixApiException {
        Company company = this.companyController.uploadLogo(companyId, this.read(logo));

        return Response.status(Response.Status.OK)
                .entity(new CompanyResponseDto(company))
                .build();
    }

    @DELETE
    @Path("/{companyId}/logo")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Delete company logo",
            description = "Removes the logo of an active company. The authenticated user must be an admin of the company."
    )
    public Response deleteLogo(
            @PathParam(RequestParam.COMPANY_ID) @NotNull(message = "companyId is required") UUID companyId) throws RentlixApiException {
        Company company = this.companyController.deleteLogo(companyId);

        return Response.status(Response.Status.OK)
                .entity(new CompanyResponseDto(company))
                .build();
    }

    @GET
    @Path("/{slug}/public")
    @PermitAll
    @RunOnVirtualThread
    @Operation(
            summary = "Get public company",
            description = "Returns the name, the slug, the logo and the available assets of an active company. Page starts at 0. Size defaults to 20 and the maximum is 100."
    )
    public Response getPublic(
            @PathParam(RequestParam.SLUG) @NotBlank(message = "slug is required") String slug,
            @QueryParam(RequestParam.PAGE) @DefaultValue("0") int page,
            @QueryParam(RequestParam.SIZE) @DefaultValue("20") int size) throws RentlixApiException {
        PublicCompany publicCompany = this.companyController.getPublicCompany(slug, page, size);

        return Response.status(Response.Status.OK)
                .entity(new PublicCompanyResponseDto(publicCompany))
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
