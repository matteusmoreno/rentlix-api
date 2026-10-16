package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.application.exception.AssetNotFoundException;
import com.mtsolutions.application.exception.AssetStatusConflictException;
import com.mtsolutions.application.exception.CompanyStatusConflictException;
import com.mtsolutions.application.exception.InvalidLesseeException;
import com.mtsolutions.application.exception.InvalidRentalPeriodException;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.exception.UserNotFoundException;
import com.mtsolutions.domain.constant.AssetStatus;
import com.mtsolutions.domain.constant.CompanyStatus;
import com.mtsolutions.domain.constant.RentalStatus;
import com.mtsolutions.domain.constant.UserRole;
import com.mtsolutions.domain.dto.request.CreateRentalRequestDto;
import com.mtsolutions.domain.entity.Asset;
import com.mtsolutions.domain.entity.Company;
import com.mtsolutions.domain.entity.CompanyMember;
import com.mtsolutions.domain.entity.Rental;
import com.mtsolutions.domain.entity.User;
import com.mtsolutions.domain.repository.AssetRepository;
import com.mtsolutions.domain.repository.CompanyMemberRepository;
import com.mtsolutions.domain.repository.RentalRepository;
import com.mtsolutions.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@ApplicationScoped
@Slf4j
public class RentalService {

    private final RentalRepository rentalRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final CompanyService companyService;
    private final DateComponent dateComponent;

    public RentalService(RentalRepository rentalRepository,
                         AssetRepository assetRepository,
                         UserRepository userRepository,
                         CompanyMemberRepository companyMemberRepository,
                         CompanyService companyService,
                         DateComponent dateComponent) {
        this.rentalRepository = rentalRepository;
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.companyMemberRepository = companyMemberRepository;
        this.companyService = companyService;
        this.dateComponent = dateComponent;
    }

    @Transactional
    public Rental createRental(CreateRentalRequestDto request) throws RentlixApiException {
        Asset asset = this.assetRepository.findByIdOptional(request.assetId())
                .orElseThrow(() -> new AssetNotFoundException(Features.CREATE_RENTAL));

        Company company = this.companyService.requireAdminCompany(asset.getCompany().getCompanyId(), Features.CREATE_RENTAL);

        if (company.getStatus() != CompanyStatus.ACTIVE) {
            throw new CompanyStatusConflictException(Features.CREATE_RENTAL);
        }
        if (!AssetStatus.RENTED.getValidPreviousStatuses().contains(asset.getStatus())) {
            throw new AssetStatusConflictException(Features.CREATE_RENTAL);
        }
        if (request.endDate().isBefore(request.startDate())) {
            throw new InvalidRentalPeriodException(Features.CREATE_RENTAL);
        }

        User lessee = this.userRepository.findByIdOptional(request.lesseeId())
                .orElseThrow(() -> new UserNotFoundException(Features.CREATE_RENTAL));

        CompanyMember member = this.companyMemberRepository.findByUserIdAndCompanyId(lessee.getUserId(), company.getCompanyId())
                .orElseThrow(() -> new InvalidLesseeException(Features.CREATE_RENTAL));

        if (member.getRole() != UserRole.LESSEE) {
            throw new InvalidLesseeException(Features.CREATE_RENTAL);
        }

        log.info("Creating rental of asset {} for lessee {}", asset.getAssetId(), lessee.getUserId());
        LocalDateTime now = this.dateComponent.now();
        Rental rental = Rental.builder()
                .asset(asset)
                .lessee(lessee)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .price(asset.getPrice())
                .pricePeriod(asset.getPricePeriod())
                .status(RentalStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        asset.setStatus(AssetStatus.RENTED);
        asset.setUpdatedAt(now);
        this.assetRepository.persist(asset);
        this.rentalRepository.persist(rental);

        log.info("Rental created with ID: {}", rental.getRentalId());
        return rental;
    }
}
