package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.application.exception.AssetNotFoundException;
import com.mtsolutions.application.exception.CompanyStatusConflictException;
import com.mtsolutions.application.exception.FinancialNotFoundException;
import com.mtsolutions.application.exception.FinancialPeriodInvalidException;
import com.mtsolutions.application.exception.FinancialReferenceInvalidException;
import com.mtsolutions.application.exception.RentalNotFoundException;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.constant.CompanyStatus;
import com.mtsolutions.domain.constant.FinancialDirection;
import com.mtsolutions.domain.dto.request.CreateFinancialRequestDto;
import com.mtsolutions.domain.dto.request.UpdateFinancialRequestDto;
import com.mtsolutions.domain.entity.Asset;
import com.mtsolutions.domain.entity.Company;
import com.mtsolutions.domain.entity.Financial;
import com.mtsolutions.domain.entity.Rental;
import com.mtsolutions.domain.model.FinancialPage;
import com.mtsolutions.domain.model.FinancialResult;
import com.mtsolutions.domain.repository.AssetRepository;
import com.mtsolutions.domain.repository.FinancialRepository;
import com.mtsolutions.domain.repository.RentalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class FinancialService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

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
        Company company = this.companyService.requireLessorCompany(request.companyId(), Features.CREATE_FINANCIAL);

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
                .direction(request.direction())
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

    @Transactional
    public Financial updateFinancial(UUID financialId, UpdateFinancialRequestDto request) throws RentlixApiException {
        Financial financial = this.financialRepository.findByIdWithReferences(financialId)
                .orElseThrow(() -> new FinancialNotFoundException(Features.UPDATE_FINANCIAL));
        Company company = this.companyService.requireLessorCompany(financial.getCompany().getCompanyId(), Features.UPDATE_FINANCIAL);
        if (company.getStatus() != CompanyStatus.ACTIVE) {
            throw new CompanyStatusConflictException(Features.UPDATE_FINANCIAL);
        }

        if (request.direction() != null) {
            financial.setDirection(request.direction());
        }
        if (request.type() != null) {
            financial.setType(request.type());
        }
        if (request.amount() != null) {
            financial.setAmount(request.amount());
        }
        if (request.description() != null) {
            financial.setDescription(request.description());
        }
        if (request.occurredOn() != null) {
            financial.setOccurredOn(request.occurredOn());
        }

        financial.setUpdatedAt(this.dateComponent.now());
        this.financialRepository.persist(financial);
        log.info("Financial updated: {}", financial.getFinancialId());
        return financial;
    }

    @Transactional
    public void deleteFinancial(UUID financialId) throws RentlixApiException {
        Financial financial = this.financialRepository.findByIdOptional(financialId)
                .orElseThrow(() -> new FinancialNotFoundException(Features.DELETE_FINANCIAL));
        Company company = this.companyService.requireLessorCompany(financial.getCompany().getCompanyId(), Features.DELETE_FINANCIAL);
        if (company.getStatus() != CompanyStatus.ACTIVE) {
            throw new CompanyStatusConflictException(Features.DELETE_FINANCIAL);
        }

        this.financialRepository.delete(financial);
        log.info("Financial deleted: {}", financialId);
    }

    @Transactional
    public Financial getFinancial(UUID financialId) throws RentlixApiException {
        Financial financial = this.financialRepository.findByIdWithReferences(financialId)
                .orElseThrow(() -> new FinancialNotFoundException(Features.GET_FINANCIAL));
        this.companyService.requireLessorCompany(financial.getCompany().getCompanyId(), Features.GET_FINANCIAL);
        return financial;
    }

    @Transactional
    public FinancialPage listFinancials(UUID companyId, UUID assetId, LocalDate from, LocalDate to, int page, int size) throws RentlixApiException {
        this.companyService.requireLessorCompany(companyId, Features.LIST_FINANCIALS);

        if (assetId != null) {
            Asset asset = this.assetRepository.findByIdOptional(assetId)
                    .orElseThrow(() -> new AssetNotFoundException(Features.LIST_FINANCIALS));
            if (!asset.getCompany().getCompanyId().equals(companyId)) {
                throw new AssetNotFoundException(Features.LIST_FINANCIALS);
            }
        }
        if (from != null && to != null && from.isAfter(to)) {
            throw new FinancialPeriodInvalidException(Features.LIST_FINANCIALS);
        }

        PageWindow window = this.pageWindow(page, size);
        List<Financial> financials = this.financialRepository.findPage(companyId, assetId, from, to, window.page(), window.size());
        long totalElements = this.financialRepository.count(companyId, assetId, from, to);
        return new FinancialPage(financials, window.page(), window.size(), totalElements);
    }

    @Transactional
    public FinancialResult financialResult(UUID companyId, UUID assetId, LocalDate from, LocalDate to) throws RentlixApiException {
        this.companyService.requireLessorCompany(companyId, Features.GET_FINANCIAL_RESULT);

        if (assetId != null) {
            Asset asset = this.assetRepository.findByIdOptional(assetId)
                    .orElseThrow(() -> new AssetNotFoundException(Features.GET_FINANCIAL_RESULT));
            if (!asset.getCompany().getCompanyId().equals(companyId)) {
                throw new AssetNotFoundException(Features.GET_FINANCIAL_RESULT);
            }
        }
        if (from != null && to != null && from.isAfter(to)) {
            throw new FinancialPeriodInvalidException(Features.GET_FINANCIAL_RESULT);
        }

        Map<FinancialDirection, BigDecimal> sums = this.financialRepository.sumByDirection(companyId, assetId, from, to);
        BigDecimal income = this.amount(sums, FinancialDirection.INCOME);
        BigDecimal expense = this.amount(sums, FinancialDirection.EXPENSE);
        return new FinancialResult(income, expense, income.subtract(expense));
    }

    private BigDecimal amount(Map<FinancialDirection, BigDecimal> sums, FinancialDirection direction) {
        BigDecimal amount = sums.get(direction);
        if (amount == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private PageWindow pageWindow(int page, int size) {
        int currentPage = page;
        int pageSize = size;
        if (currentPage < 0) {
            currentPage = 0;
        }
        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }
        return new PageWindow(currentPage, pageSize);
    }

    private record PageWindow(int page, int size) {}
}
