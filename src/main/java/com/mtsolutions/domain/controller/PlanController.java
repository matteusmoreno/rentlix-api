package com.mtsolutions.domain.controller;

import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.dto.request.CreatePlanRequestDto;
import com.mtsolutions.domain.dto.request.UpdatePlanRequestDto;
import com.mtsolutions.domain.entity.Plan;
import com.mtsolutions.domain.service.PlanService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    public Plan createPlan(CreatePlanRequestDto request) throws RentlixApiException {
        return this.planService.createPlan(request);
    }

    public Plan updatePlan(UUID planId, UpdatePlanRequestDto request) throws RentlixApiException {
        return this.planService.updatePlan(planId, request);
    }

    public void deletePlan(UUID planId) throws RentlixApiException {
        this.planService.deletePlan(planId);
    }

    public List<Plan> listPlans() throws RentlixApiException {
        return this.planService.listPlans();
    }

    public Plan getPlan(UUID planId) throws RentlixApiException {
        return this.planService.getPlan(planId);
    }
}
