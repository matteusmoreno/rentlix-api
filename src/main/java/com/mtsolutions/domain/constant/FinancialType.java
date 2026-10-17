package com.mtsolutions.domain.constant;

import lombok.Getter;

@Getter
public enum FinancialType {
    INCOME("Income"),
    EXPENSE("Expense");

    private final String displayName;

    FinancialType(String displayName) {
        this.displayName = displayName;
    }
}
