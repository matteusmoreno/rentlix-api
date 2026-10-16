package com.mtsolutions.domain.constant;

import lombok.Getter;

import java.util.List;

@Getter
public enum AssetStatus {
    AVAILABLE("Available"),
    RENTED("Rented"),
    MAINTENANCE("Maintenance"),
    INACTIVE("Inactive");

    private final String displayName;

    AssetStatus(String displayName) {
        this.displayName = displayName;
    }

    public List<AssetStatus> getValidPreviousStatuses() {
        return switch (this) {
            case AVAILABLE -> List.of(RENTED, MAINTENANCE, INACTIVE);
            case RENTED, MAINTENANCE, INACTIVE -> List.of(AVAILABLE);
        };
    }
}
