package com.mtsolutions.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AssetCategory {
    VEHICLE("Vehicle"),
    PROPERTY("Property");

    private final String displayName;
}
