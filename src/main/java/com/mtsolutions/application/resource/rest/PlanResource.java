package com.mtsolutions.application.resource.rest;

import com.mtsolutions.application.common.RequestParam;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.resource.rest.examples.PlanExamples;
import com.mtsolutions.domain.controller.PlanController;
import com.mtsolutions.domain.dto.request.CreatePlanRequestDto;
import com.mtsolutions.domain.dto.request.UpdatePlanRequestDto;
import com.mtsolutions.domain.dto.response.PlanResponseDto;
import com.mtsolutions.domain.entity.Plan;
import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@RequestScoped
@Path("/v1/plans")
@Tag(name = "Plans", description = "Endpoints for managing plans")
public class PlanResource {

    private final PlanController planController;

    public PlanResource(PlanController planController) {
        this.planController = planController;
    }

    @POST
    @Path("/create")
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    @Operation(
            summary = "Create plan",
            description = "Creates a paid plan. The authenticated user must be a platform admin. The FREE plan already exists and is not created here."
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = CreatePlanRequestDto.class),
                    examples = @ExampleObject(
                            name = "Create Plan",
                            value = PlanExamples.CREATE_PLAN_EXAMPLE
                    )
            )
    )
    public Response create(@Valid CreatePlanRequestDto request) throws RentlixApiException {
        Plan plan = this.planController.createPlan(request);

        return Response.status(Response.Status.CREATED)
                .entity(new PlanResponseDto(plan))
                .build();
    }

    @PUT
    @Path("/{planId}/update")
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    @Operation(
            summary = "Update plan",
            description = "Updates a plan. A null field is left unchanged and a blank name is rejected. The FREE plan cannot be changed. The authenticated user must be a platform admin."
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = UpdatePlanRequestDto.class),
                    examples = @ExampleObject(
                            name = "Update Plan",
                            value = PlanExamples.UPDATE_PLAN_EXAMPLE
                    )
            )
    )
    public Response update(
            @PathParam(RequestParam.PLAN_ID) @NotNull(message = "planId is required") UUID planId,
            @Valid UpdatePlanRequestDto request) throws RentlixApiException {
        Plan plan = this.planController.updatePlan(planId, request);

        return Response.status(Response.Status.OK)
                .entity(new PlanResponseDto(plan))
                .build();
    }

    @DELETE
    @Path("/{planId}")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Delete plan",
            description = "Removes a plan that no company uses. The FREE plan cannot be removed. The authenticated user must be a platform admin."
    )
    public Response delete(@PathParam(RequestParam.PLAN_ID) @NotNull(message = "planId is required") UUID planId) throws RentlixApiException {
        this.planController.deletePlan(planId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "List plans",
            description = "Lists the plans, lowest price first. The FREE plan is included."
    )
    public Response list() throws RentlixApiException {
        List<PlanResponseDto> plans = this.planController.listPlans().stream()
                .map(PlanResponseDto::new)
                .toList();

        return Response.status(Response.Status.OK)
                .entity(plans)
                .build();
    }

    @GET
    @Path("/{planId}")
    @Authenticated
    @RunOnVirtualThread
    @Operation(
            summary = "Get plan",
            description = "Returns a plan."
    )
    public Response get(@PathParam(RequestParam.PLAN_ID) @NotNull(message = "planId is required") UUID planId) throws RentlixApiException {
        Plan plan = this.planController.getPlan(planId);

        return Response.status(Response.Status.OK)
                .entity(new PlanResponseDto(plan))
                .build();
    }
}
