package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.entity.Plan;

import java.math.BigDecimal;
import java.util.UUID;

public record PlanResponseDto(
        UUID planId,
        String name,
        BigDecimal price,
        Boolean fixed
) {
    public PlanResponseDto(Plan plan) {
        this(
                plan.getPlanId(),
                plan.getName(),
                plan.getPrice(),
                plan.getFixed()
        );
    }
}
