package com.mtsolutions.domain.constant;

import lombok.Getter;

import java.util.List;

@Getter
public enum RentalStatus {
    ACTIVE("Active"),
    FINISHED("Finished");

    private final String displayName;

    RentalStatus(String displayName) {
        this.displayName = displayName;
    }

    public List<RentalStatus> getValidPreviousStatuses() {
        return switch (this) {
            case ACTIVE -> List.of();
            case FINISHED -> List.of(ACTIVE);
        };
    }
}
