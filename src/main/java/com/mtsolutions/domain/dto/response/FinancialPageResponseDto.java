package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.model.FinancialPage;

import java.util.List;

public record FinancialPageResponseDto(
        List<FinancialResponseDto> financials,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public FinancialPageResponseDto(FinancialPage financialPage) {
        this(
                financialPage.financials().stream().map(FinancialResponseDto::new).toList(),
                financialPage.page(),
                financialPage.size(),
                financialPage.totalElements(),
                financialPage.totalPages()
        );
    }
}
