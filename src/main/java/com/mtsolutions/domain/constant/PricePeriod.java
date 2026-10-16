package com.mtsolutions.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PricePeriod {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
    SEMESTER("Semester"),
    YEAR("Year");

    private final String displayName;
}
