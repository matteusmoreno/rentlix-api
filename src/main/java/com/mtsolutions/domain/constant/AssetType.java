package com.mtsolutions.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AssetType {
    MOTORCYCLE("Motorcycle", AssetCategory.VEHICLE),
    CAR("Car", AssetCategory.VEHICLE),
    HOUSE("House", AssetCategory.PROPERTY),
    APARTMENT("Apartment", AssetCategory.PROPERTY);

    private final String displayName;
    private final AssetCategory category;
}
