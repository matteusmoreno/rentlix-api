package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.application.exception.AssetNotFoundException;
import com.mtsolutions.application.exception.CompanyStatusConflictException;
import com.mtsolutions.application.exception.FinancialReferenceInvalidException;
import com.mtsolutions.application.exception.RentalNotFoundException;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.constant.CompanyStatus;
import com.mtsolutions.domain.dto.request.CreateFinancialRequestDto;
import com.mtsolutions.domain.entity.Asset;
import com.mtsolutions.domain.entity.Company;
import com.mtsolutions.domain.entity.Financial;
import com.mtsolutions.domain.entity.Rental;
import com.mtsolutions.domain.repository.AssetRepository;
import com.mtsolutions.domain.repository.FinancialRepository;
import com.mtsolutions.domain.repository.RentalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@ApplicationScoped
@Slf4j
public class FinancialService {

    private final FinancialRepository financialRepository;
    private final AssetRepository assetRepository;
    private final RentalRepository rentalRepository;
    private final CompanyService companyService;
    private final DateComponent dateComponent;

    public FinancialService(FinancialRepository financialRepository,
                            AssetRepository assetRepository,
                            RentalRepository rentalRepository,
                            CompanyService companyService,
                            DateComponent dateComponent) {
        this.financialRepository = financialRepository;
        this.assetRepository = assetRepository;
        this.rentalRepository = rentalRepository;
        this.companyService = companyService;
        this.dateComponent = dateComponent;
    }

    @Transactional
    public Financial createFinancial(CreateFinancialRequestDto request) throws RentlixApiException {
        Company company = this.companyService.requireAdminCompany(request.companyId(), Features.CREATE_FINANCIAL);

        if (company.getStatus() != CompanyStatus.ACTIVE) {
            throw new CompanyStatusConflictException(Features.CREATE_FINANCIAL);
        }

        Asset asset = null;
        if (request.assetId() != null) {
            asset = this.assetRepository.findByIdOptional(request.assetId())
                    .orElseThrow(() -> new AssetNotFoundException(Features.CREATE_FINANCIAL));
            if (!asset.getCompany().getCompanyId().equals(company.getCompanyId())) {
                throw new AssetNotFoundException(Features.CREATE_FINANCIAL);
            }
        }

        Rental rental = null;
        if (request.rentalId() != null) {
            rental = this.rentalRepository.findByIdOptional(request.rentalId())
                    .orElseThrow(() -> new RentalNotFoundException(Features.CREATE_FINANCIAL));
            Asset rentalAsset = rental.getAsset();
            if (!rentalAsset.getCompany().getCompanyId().equals(company.getCompanyId())) {
                throw new RentalNotFoundException(Features.CREATE_FINANCIAL);
            }
            if (asset != null && !rentalAsset.getAssetId().equals(asset.getAssetId())) {
                throw new FinancialReferenceInvalidException(Features.CREATE_FINANCIAL);
            }
            asset = rentalAsset;
        }

        log.info("Creating financial for company {}", company.getCompanyId());
        LocalDateTime now = this.dateComponent.now();
        Financial financial = Financial.builder()
                .company(company)
                .asset(asset)
                .rental(rental)
                .type(request.type())
                .amount(request.amount())
                .description(request.description())
                .occurredOn(request.occurredOn())
                .createdAt(now)
                .updatedAt(now)
                .build();

        this.financialRepository.persist(financial);
        log.info("Financial created: {}", financial.getFinancialId());
        return financial;
    }
}
