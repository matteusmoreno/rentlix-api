package com.mtsolutions.domain.controller;

import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.dto.request.CreateFinancialRequestDto;
import com.mtsolutions.domain.entity.Financial;
import com.mtsolutions.domain.service.FinancialService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FinancialController {

    private final FinancialService financialService;

    public FinancialController(FinancialService financialService) {
        this.financialService = financialService;
    }

    public Financial createFinancial(CreateFinancialRequestDto request) throws RentlixApiException {
        return this.financialService.createFinancial(request);
    }
}
