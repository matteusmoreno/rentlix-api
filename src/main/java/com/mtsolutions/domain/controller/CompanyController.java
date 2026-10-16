package com.mtsolutions.domain.controller;

import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.dto.request.AddCompanyMemberRequestDto;
import com.mtsolutions.domain.dto.request.CreateCompanyRequestDto;
import com.mtsolutions.domain.entity.Company;
import com.mtsolutions.domain.entity.CompanyMember;
import com.mtsolutions.domain.service.CompanyService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    public Company createCompany(CreateCompanyRequestDto request) throws RentlixApiException {
        return this.companyService.createCompany(request);
    }

    public List<CompanyMember> listCompanies() throws RentlixApiException {
        return this.companyService.listCompanies();
    }

    public CompanyMember addMember(UUID companyId, AddCompanyMemberRequestDto request) throws RentlixApiException {
        return this.companyService.addMember(companyId, request);
    }

    public List<CompanyMember> listMembers(UUID companyId) throws RentlixApiException {
        return this.companyService.listMembers(companyId);
    }
}
