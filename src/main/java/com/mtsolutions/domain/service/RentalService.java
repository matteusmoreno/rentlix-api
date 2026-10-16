package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.component.ContextComponent;
import com.mtsolutions.application.component.ContextUser;
import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.application.exception.AccessDeniedException;
import com.mtsolutions.application.exception.AssetNotFoundException;
import com.mtsolutions.application.exception.AssetStatusConflictException;
import com.mtsolutions.application.exception.CompanyStatusConflictException;
import com.mtsolutions.application.exception.InvalidLesseeException;
import com.mtsolutions.application.exception.InvalidRentalPeriodException;
import com.mtsolutions.application.exception.RentalNotFoundException;
import com.mtsolutions.application.exception.RentalStatusConflictException;
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
import com.mtsolutions.domain.model.RentalPage;
import com.mtsolutions.domain.repository.AssetRepository;
import com.mtsolutions.domain.repository.CompanyMemberRepository;
import com.mtsolutions.domain.repository.RentalRepository;
import com.mtsolutions.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class RentalService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final RentalRepository rentalRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final CompanyService companyService;
    private final ContextComponent contextComponent;
    private final DateComponent dateComponent;

    public RentalService(RentalRepository rentalRepository,
                         AssetRepository assetRepository,
                         UserRepository userRepository,
                         CompanyMemberRepository companyMemberRepository,
                         CompanyService companyService,
                         ContextComponent contextComponent,
                         DateComponent dateComponent) {
        this.rentalRepository = rentalRepository;
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.companyMemberRepository = companyMemberRepository;
        this.companyService = companyService;
        this.contextComponent = contextComponent;
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

    @Transactional
    public Rental finishRental(UUID rentalId) throws RentlixApiException {
        Rental rental = this.rentalRepository.findByIdWithAssetAndLessee(rentalId)
                .orElseThrow(() -> new RentalNotFoundException(Features.FINISH_RENTAL));
        Asset asset = rental.getAsset();
        this.companyService.requireAdminCompany(asset.getCompany().getCompanyId(), Features.FINISH_RENTAL);

        if (!RentalStatus.FINISHED.getValidPreviousStatuses().contains(rental.getStatus())) {
            throw new RentalStatusConflictException(Features.FINISH_RENTAL);
        }
        if (asset.getStatus() != AssetStatus.RENTED) {
            throw new AssetStatusConflictException(Features.FINISH_RENTAL);
        }

        LocalDateTime now = this.dateComponent.now();
        rental.setStatus(RentalStatus.FINISHED);
        rental.setUpdatedAt(now);
        asset.setStatus(AssetStatus.AVAILABLE);
        asset.setUpdatedAt(now);
        this.rentalRepository.persist(rental);
        this.assetRepository.persist(asset);

        log.info("Rental finished: {}", rental.getRentalId());
        return rental;
    }

    @Transactional
    public RentalPage listRentals(UUID companyId, int page, int size) throws RentlixApiException {
        this.companyService.requireMemberCompany(companyId, Features.LIST_RENTALS);
        ContextUser currentUser = this.contextComponent.getCurrentUser();
        CompanyMember member = this.companyMemberRepository.findByUserIdAndCompanyId(currentUser.getUserId(), companyId)
                .orElseThrow(() -> new AccessDeniedException(Features.LIST_RENTALS));

        PageWindow window = this.pageWindow(page, size);
        List<Rental> rentals;
        long totalElements;
        if (member.getRole() == UserRole.ADMIN) {
            rentals = this.rentalRepository.findPageByCompany(companyId, window.page(), window.size());
            totalElements = this.rentalRepository.countByCompany(companyId);
        } else if (member.getRole() == UserRole.LESSEE) {
            rentals = this.rentalRepository.findPageByCompanyAndLessee(companyId, currentUser.getUserId(), window.page(), window.size());
            totalElements = this.rentalRepository.countByCompanyAndLessee(companyId, currentUser.getUserId());
        } else {
            throw new AccessDeniedException(Features.LIST_RENTALS);
        }
        return new RentalPage(rentals, window.page(), window.size(), totalElements);
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
