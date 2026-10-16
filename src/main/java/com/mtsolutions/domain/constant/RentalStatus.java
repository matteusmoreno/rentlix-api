package com.mtsolutions.domain.constant;

import lombok.Getter;

@Getter
public enum RentalStatus {
    ACTIVE("Active");

    private final String displayName;

    RentalStatus(String displayName) {
        this.displayName = displayName;
    }
}
