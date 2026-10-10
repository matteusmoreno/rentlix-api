package com.mtsolutions.domain.constant;

import lombok.Getter;

@Getter
public enum CompanyStatus {
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    CANCELED("Canceled");

    private final String displayName;

    CompanyStatus(String displayName) {
        this.displayName = displayName;
    }
}
