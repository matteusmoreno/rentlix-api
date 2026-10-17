package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.model.FinancialResult;

import java.math.BigDecimal;

public record FinancialResultResponseDto(
        BigDecimal income,
        BigDecimal expense,
        BigDecimal result
) {
    public FinancialResultResponseDto(FinancialResult financialResult) {
        this(
                financialResult.income(),
                financialResult.expense(),
                financialResult.result()
        );
    }
}
