package com.mtsolutions.domain.constant;

import lombok.Getter;

@Getter
public enum FinancialDirection {
    INCOME("Income"),
    EXPENSE("Expense");

    private final String displayName;

    FinancialDirection(String displayName) {
        this.displayName = displayName;
    }
}
