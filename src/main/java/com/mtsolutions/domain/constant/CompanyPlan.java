package com.mtsolutions.domain.constant;

import lombok.Getter;

@Getter
public enum CompanyPlan {
    FREE("Free"),
    PRO("Pro"),
    PREMIUM("Premium");

    private final String displayName;

    CompanyPlan(String displayName) {
        this.displayName = displayName;
    }
}
