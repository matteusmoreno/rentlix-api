package com.mtsolutions.domain.model;

import java.math.BigDecimal;

public record FinancialResult(
        BigDecimal income,
        BigDecimal expense,
        BigDecimal result
) {}
