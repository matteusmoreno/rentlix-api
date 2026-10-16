package com.mtsolutions.domain.constant;

import lombok.Getter;

import java.util.List;

@Getter
public enum CompanyStatus {
    ACTIVE("Active", false),
    SUSPENDED("Suspended", false),
    CANCELED("Canceled", true);

    private final String displayName;
    private final boolean finalStatus;

    CompanyStatus(String displayName, boolean finalStatus) {
        this.displayName = displayName;
        this.finalStatus = finalStatus;
    }

    public List<CompanyStatus> getValidPreviousStatuses() {
        return switch (this) {
            case ACTIVE -> List.of(SUSPENDED);
            case SUSPENDED -> List.of(ACTIVE);
            case CANCELED -> List.of(ACTIVE, SUSPENDED);
        };
    }
}
