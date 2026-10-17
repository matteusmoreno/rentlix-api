package com.mtsolutions.domain.constant;

import lombok.Getter;

@Getter
public enum FinancialType {
    RENT("Rent"),
    DEPOSIT("Deposit"),
    MAINTENANCE("Maintenance"),
    INSURANCE("Insurance"),
    FINE("Fine"),
    TAX("Tax"),
    CONDO("Condo"),
    OTHER("Other");

    private final String displayName;

    FinancialType(String displayName) {
        this.displayName = displayName;
    }
}
