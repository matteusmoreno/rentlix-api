package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.constant.AssetCategory;
import com.mtsolutions.domain.entity.ContractTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContractTemplateResponseDto(
        UUID contractTemplateId,
        UUID companyId,
        AssetCategory category,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public ContractTemplateResponseDto(ContractTemplate template) {
        this(
                template.getContractTemplateId(),
                template.getCompany().getCompanyId(),
                template.getCategory(),
                template.getContent(),
                template.getCreatedAt(),
                template.getUpdatedAt()
        );
    }
}
