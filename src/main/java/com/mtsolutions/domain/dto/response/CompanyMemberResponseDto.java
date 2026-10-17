package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.constant.CompanyPlan;
import com.mtsolutions.domain.constant.CompanyStatus;
import com.mtsolutions.domain.constant.UserRole;
import com.mtsolutions.domain.entity.CompanyMember;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyMemberResponseDto(
        UUID companyId,
        String name,
        String slug,
        String logoUrl,
        String cnpj,
        CompanyPlan plan,
        CompanyStatus status,
        UserRole role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public CompanyMemberResponseDto(CompanyMember member) {
        this(
                member.getCompany().getCompanyId(),
                member.getCompany().getName(),
                member.getCompany().getSlug(),
                member.getCompany().getLogoUrl(),
                member.getCompany().getCnpj(),
                member.getCompany().getPlan(),
                member.getCompany().getStatus(),
                member.getRole(),
                member.getCompany().getCreatedAt(),
                member.getCompany().getUpdatedAt()
        );
    }
}
