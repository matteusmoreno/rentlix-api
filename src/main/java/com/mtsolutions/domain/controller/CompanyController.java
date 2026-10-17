package com.mtsolutions.domain.controller;

import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.constant.AssetCategory;
import com.mtsolutions.domain.dto.request.AddCompanyMemberRequestDto;
import com.mtsolutions.domain.dto.request.CreateCompanyRequestDto;
import com.mtsolutions.domain.dto.request.SaveContractTemplateRequestDto;
import com.mtsolutions.domain.entity.Company;
import com.mtsolutions.domain.entity.CompanyMember;
import com.mtsolutions.domain.entity.ContractTemplate;
import com.mtsolutions.domain.model.PublicCompany;
import com.mtsolutions.domain.service.CompanyService;
import com.mtsolutions.domain.service.ContractTemplateService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CompanyController {

    private final CompanyService companyService;
    private final ContractTemplateService contractTemplateService;

    public CompanyController(CompanyService companyService, ContractTemplateService contractTemplateService) {
        this.companyService = companyService;
        this.contractTemplateService = contractTemplateService;
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

    public Company suspendCompany(UUID companyId) throws RentlixApiException {
        return this.companyService.suspendCompany(companyId);
    }

    public Company cancelCompany(UUID companyId) throws RentlixApiException {
        return this.companyService.cancelCompany(companyId);
    }

    public Company reactivateCompany(UUID companyId) throws RentlixApiException {
        return this.companyService.reactivateCompany(companyId);
    }

    public ContractTemplate saveContractTemplate(UUID companyId, AssetCategory category, SaveContractTemplateRequestDto request) throws RentlixApiException {
        return this.contractTemplateService.save(companyId, category, request);
    }

    public ContractTemplate getContractTemplate(UUID companyId, AssetCategory category) throws RentlixApiException {
        return this.contractTemplateService.get(companyId, category);
    }

    public Company uploadLogo(UUID companyId, byte[] fileBytes) throws RentlixApiException {
        return this.companyService.uploadLogo(companyId, fileBytes);
    }

    public Company deleteLogo(UUID companyId) throws RentlixApiException {
        return this.companyService.deleteLogo(companyId);
    }

    public PublicCompany getPublicCompany(String slug, int page, int size) throws RentlixApiException {
        return this.companyService.getPublicCompany(slug, page, size);
    }
}
