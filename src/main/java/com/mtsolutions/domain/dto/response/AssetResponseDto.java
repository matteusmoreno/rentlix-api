package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.constant.AssetStatus;
import com.mtsolutions.domain.constant.AssetType;
import com.mtsolutions.domain.constant.PricePeriod;
import com.mtsolutions.domain.entity.Asset;
import com.mtsolutions.domain.model.PropertyDetails;
import com.mtsolutions.domain.model.VehicleDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AssetResponseDto(
        UUID assetId,
        UUID companyId,
        AssetType type,
        String name,
        String description,
        String imageUrl,
        List<String> gallery,
        BigDecimal price,
        PricePeriod pricePeriod,
        VehicleDetails vehicle,
        PropertyDetails property,
        AssetStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public AssetResponseDto(Asset asset) {
        this(
                asset.getAssetId(),
                asset.getCompany().getCompanyId(),
                asset.getType(),
                asset.getName(),
                asset.getDescription(),
                asset.getImageUrl(),
                List.copyOf(asset.getGallery()),
                asset.getPrice(),
                asset.getPricePeriod(),
                asset.getVehicle(),
                asset.getProperty(),
                asset.getStatus(),
                asset.getCreatedAt(),
                asset.getUpdatedAt()
        );
    }
}
