package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.constant.FinancialDirection;
import com.mtsolutions.domain.constant.FinancialType;
import com.mtsolutions.domain.entity.Financial;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record FinancialResponseDto(
        UUID financialId,
        UUID companyId,
        UUID assetId,
        UUID rentalId,
        FinancialDirection direction,
        FinancialType type,
        BigDecimal amount,
        String description,
        LocalDate occurredOn,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public FinancialResponseDto(Financial financial) {
        this(
                financial.getFinancialId(),
                financial.getCompany().getCompanyId(),
                financial.getAsset() == null ? null : financial.getAsset().getAssetId(),
                financial.getRental() == null ? null : financial.getRental().getRentalId(),
                financial.getDirection(),
                financial.getType(),
                financial.getAmount(),
                financial.getDescription(),
                financial.getOccurredOn(),
                financial.getCreatedAt(),
                financial.getUpdatedAt()
        );
    }
}
