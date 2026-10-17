package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.component.ContextComponent;
import com.mtsolutions.application.component.ContextUser;
import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.application.exception.AccessDeniedException;
import com.mtsolutions.application.exception.PlanAlreadyExistsException;
import com.mtsolutions.application.exception.PlanFixedException;
import com.mtsolutions.application.exception.PlanInUseException;
import com.mtsolutions.application.exception.PlanNotFoundException;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.dto.request.CreatePlanRequestDto;
import com.mtsolutions.domain.dto.request.UpdatePlanRequestDto;
import com.mtsolutions.domain.entity.Plan;
import com.mtsolutions.domain.repository.CompanyRepository;
import com.mtsolutions.domain.repository.PlanRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class PlanService {

    private final PlanRepository planRepository;
    private final CompanyRepository companyRepository;
    private final ContextComponent contextComponent;
    private final DateComponent dateComponent;

    public PlanService(PlanRepository planRepository,
                       CompanyRepository companyRepository,
                       ContextComponent contextComponent,
                       DateComponent dateComponent) {
        this.planRepository = planRepository;
        this.companyRepository = companyRepository;
        this.contextComponent = contextComponent;
        this.dateComponent = dateComponent;
    }

    @Transactional
    public Plan createPlan(CreatePlanRequestDto request) throws RentlixApiException {
        this.requireAdmin(Features.CREATE_PLAN);
        log.info("Creating plan: {}", request.name());
        if (this.planRepository.existsByName(request.name())) {
            throw new PlanAlreadyExistsException(Features.CREATE_PLAN);
        }

        Plan plan = Plan.builder()
                .name(request.name())
                .price(request.price())
                .fixed(false)
                .createdAt(this.dateComponent.now())
                .updatedAt(this.dateComponent.now())
                .build();
        this.planRepository.persist(plan);
        log.info("Plan created with ID: {}", plan.getPlanId());
        return plan;
    }

    @Transactional
    public Plan updatePlan(UUID planId, UpdatePlanRequestDto request) throws RentlixApiException {
        this.requireAdmin(Features.UPDATE_PLAN);
        Plan plan = this.planRepository.findByIdOptional(planId)
                .orElseThrow(() -> new PlanNotFoundException(Features.UPDATE_PLAN));
        if (Boolean.TRUE.equals(plan.getFixed())) {
            throw new PlanFixedException(Features.UPDATE_PLAN);
        }
        if (request.name() != null && this.planRepository.existsOtherByName(planId, request.name())) {
            throw new PlanAlreadyExistsException(Features.UPDATE_PLAN);
        }

        if (request.name() != null) {
            plan.setName(request.name());
        }
        if (request.price() != null) {
            plan.setPrice(request.price());
        }
        plan.setUpdatedAt(this.dateComponent.now());
        this.planRepository.persist(plan);
        log.info("Plan updated: {}", plan.getPlanId());
        return plan;
    }

    @Transactional
    public void deletePlan(UUID planId) throws RentlixApiException {
        this.requireAdmin(Features.DELETE_PLAN);
        Plan plan = this.planRepository.findByIdOptional(planId)
                .orElseThrow(() -> new PlanNotFoundException(Features.DELETE_PLAN));
        if (Boolean.TRUE.equals(plan.getFixed())) {
            throw new PlanFixedException(Features.DELETE_PLAN);
        }
        if (this.companyRepository.count("plan.planId", planId) > 0) {
            throw new PlanInUseException(Features.DELETE_PLAN);
        }
        this.planRepository.delete(plan);
        log.info("Plan deleted: {}", planId);
    }

    public List<Plan> listPlans() throws RentlixApiException {
        ContextUser currentUser = this.contextComponent.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException(Features.LIST_PLANS);
        }
        return this.planRepository.listAllByPrice();
    }

    public Plan getPlan(UUID planId) throws RentlixApiException {
        ContextUser currentUser = this.contextComponent.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException(Features.GET_PLAN);
        }
        return this.planRepository.findByIdOptional(planId)
                .orElseThrow(() -> new PlanNotFoundException(Features.GET_PLAN));
    }

    private void requireAdmin(Features feature) throws RentlixApiException {
        ContextUser currentUser = this.contextComponent.getCurrentUser();
        if (currentUser == null || !Boolean.TRUE.equals(currentUser.getAdmin())) {
            throw new AccessDeniedException(feature);
        }
    }
}
