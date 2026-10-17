package com.mtsolutions.domain.controller;

import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.dto.request.CreateFinancialRequestDto;
import com.mtsolutions.domain.entity.Financial;
import com.mtsolutions.domain.model.FinancialPage;
import com.mtsolutions.domain.model.FinancialResult;
import com.mtsolutions.domain.service.FinancialService;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
public class FinancialController {

    private final FinancialService financialService;

    public FinancialController(FinancialService financialService) {
        this.financialService = financialService;
    }

    public Financial createFinancial(CreateFinancialRequestDto request) throws RentlixApiException {
        return this.financialService.createFinancial(request);
    }

    public Financial getFinancial(UUID financialId) throws RentlixApiException {
        return this.financialService.getFinancial(financialId);
    }

    public FinancialPage listFinancials(UUID companyId, UUID assetId, LocalDate from, LocalDate to, int page, int size) throws RentlixApiException {
        return this.financialService.listFinancials(companyId, assetId, from, to, page, size);
    }

    public FinancialResult financialResult(UUID companyId, UUID assetId, LocalDate from, LocalDate to) throws RentlixApiException {
        return this.financialService.financialResult(companyId, assetId, from, to);
    }
}
