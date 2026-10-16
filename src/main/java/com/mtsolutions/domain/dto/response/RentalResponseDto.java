package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.constant.PricePeriod;
import com.mtsolutions.domain.constant.RentalStatus;
import com.mtsolutions.domain.entity.Rental;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record RentalResponseDto(
        UUID rentalId,
        UUID assetId,
        UUID companyId,
        UUID lesseeId,
        String lesseeName,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal price,
        PricePeriod pricePeriod,
        String contractUrl,
        RentalStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public RentalResponseDto(Rental rental) {
        this(
                rental.getRentalId(),
                rental.getAsset().getAssetId(),
                rental.getAsset().getCompany().getCompanyId(),
                rental.getLessee().getUserId(),
                rental.getLessee().getName(),
                rental.getStartDate(),
                rental.getEndDate(),
                rental.getPrice(),
                rental.getPricePeriod(),
                rental.getContractUrl(),
                rental.getStatus(),
                rental.getCreatedAt(),
                rental.getUpdatedAt()
        );
    }
}
