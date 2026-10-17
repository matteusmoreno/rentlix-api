package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.constant.CompanyPlan;
import com.mtsolutions.domain.constant.CompanyStatus;
import com.mtsolutions.domain.entity.Company;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyResponseDto(
        UUID companyId,
        String name,
        String slug,
        String logoUrl,
        String cnpj,
        CompanyPlan plan,
        CompanyStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public CompanyResponseDto(Company company) {
        this(
                company.getCompanyId(),
                company.getName(),
                company.getSlug(),
                company.getLogoUrl(),
                company.getCnpj(),
                company.getPlan(),
                company.getStatus(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );
    }
}
