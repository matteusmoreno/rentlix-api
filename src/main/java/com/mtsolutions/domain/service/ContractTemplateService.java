package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.application.exception.CompanyStatusConflictException;
import com.mtsolutions.application.exception.ContractTemplateNotFoundException;
import com.mtsolutions.application.exception.RentalNotFoundException;
import com.mtsolutions.application.exception.RentalStatusConflictException;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.constant.AssetCategory;
import com.mtsolutions.domain.constant.CompanyStatus;
import com.mtsolutions.domain.constant.RentalStatus;
import com.mtsolutions.domain.dto.request.SaveContractTemplateRequestDto;
import com.mtsolutions.domain.entity.Company;
import com.mtsolutions.domain.entity.ContractTemplate;
import com.mtsolutions.domain.entity.Rental;
import com.mtsolutions.domain.repository.ContractTemplateRepository;
import com.mtsolutions.domain.repository.RentalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class ContractTemplateService {

    private final ContractTemplateRepository contractTemplateRepository;
    private final RentalRepository rentalRepository;
    private final CompanyService companyService;
    private final ContractPdf contractPdf;
    private final DateComponent dateComponent;

    public ContractTemplateService(ContractTemplateRepository contractTemplateRepository,
                                   RentalRepository rentalRepository,
                                   CompanyService companyService,
                                   ContractPdf contractPdf,
                                   DateComponent dateComponent) {
        this.contractTemplateRepository = contractTemplateRepository;
        this.rentalRepository = rentalRepository;
        this.companyService = companyService;
        this.contractPdf = contractPdf;
        this.dateComponent = dateComponent;
    }

    @Transactional
    public ContractTemplate save(UUID companyId, AssetCategory category, SaveContractTemplateRequestDto request) throws RentlixApiException {
        Company company = this.companyService.requireLessorCompany(companyId, Features.SAVE_CONTRACT_TEMPLATE);
        if (company.getStatus() != CompanyStatus.ACTIVE) {
            throw new CompanyStatusConflictException(Features.SAVE_CONTRACT_TEMPLATE);
        }

        LocalDateTime now = this.dateComponent.now();
        ContractTemplate template = this.contractTemplateRepository.findByCompanyAndCategory(companyId, category)
                .orElseGet(() -> ContractTemplate.builder()
                        .company(company)
                        .category(category)
                        .createdAt(now)
                        .build());
        template.setContent(request.content());
        template.setUpdatedAt(now);
        this.contractTemplateRepository.persist(template);
        log.info("Contract template saved for company {} and category {}", companyId, category);
        return template;
    }

    @Transactional
    public ContractTemplate get(UUID companyId, AssetCategory category) throws RentlixApiException {
        this.companyService.requireLessorCompany(companyId, Features.GET_CONTRACT_TEMPLATE);
        return this.contractTemplateRepository.findByCompanyAndCategory(companyId, category)
                .orElseThrow(() -> new ContractTemplateNotFoundException(Features.GET_CONTRACT_TEMPLATE));
    }

    @Transactional
    public byte[] generate(UUID rentalId) throws RentlixApiException {
        Rental rental = this.rentalRepository.findByIdWithAssetAndLessee(rentalId)
                .orElseThrow(() -> new RentalNotFoundException(Features.GENERATE_RENTAL_CONTRACT));
        this.companyService.requireLessorCompany(rental.getAsset().getCompany().getCompanyId(), Features.GENERATE_RENTAL_CONTRACT);
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new RentalStatusConflictException(Features.GENERATE_RENTAL_CONTRACT);
        }

        AssetCategory category = rental.getAsset().getType().getCategory();
        ContractTemplate template = this.contractTemplateRepository
                .findByCompanyAndCategory(rental.getAsset().getCompany().getCompanyId(), category)
                .orElseThrow(() -> new ContractTemplateNotFoundException(Features.GENERATE_RENTAL_CONTRACT));
        log.info("Generating contract for rental {}", rentalId);
        return this.contractPdf.render(template.getContent(), rental);
    }
}
