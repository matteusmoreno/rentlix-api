package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.constant.AssetType;
import com.mtsolutions.domain.constant.PricePeriod;
import com.mtsolutions.domain.entity.Asset;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PublicAssetResponseDto(
        UUID assetId,
        UUID companyId,
        String companyName,
        AssetType type,
        String name,
        String description,
        String imageUrl,
        List<String> gallery,
        BigDecimal price,
        PricePeriod pricePeriod,
        PublicVehicleResponseDto vehicle,
        PublicPropertyResponseDto property
) {
    public PublicAssetResponseDto(Asset asset) {
        this(
                asset.getAssetId(),
                asset.getCompany().getCompanyId(),
                asset.getCompany().getName(),
                asset.getType(),
                asset.getName(),
                asset.getDescription(),
                asset.getImageUrl(),
                List.copyOf(asset.getGallery()),
                asset.getPrice(),
                asset.getPricePeriod(),
                asset.getVehicle() == null ? null : new PublicVehicleResponseDto(asset.getVehicle()),
                asset.getProperty() == null ? null : new PublicPropertyResponseDto(asset.getProperty())
        );
    }
}
