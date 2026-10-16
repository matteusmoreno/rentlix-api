package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.application.exception.AssetAlreadyExistsException;
import com.mtsolutions.application.exception.CompanyStatusConflictException;
import com.mtsolutions.application.exception.InvalidAssetDetailsException;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.constant.AssetCategory;
import com.mtsolutions.domain.constant.AssetStatus;
import com.mtsolutions.domain.constant.CompanyStatus;
import com.mtsolutions.domain.dto.AssetDetails;
import com.mtsolutions.domain.dto.request.CreateAssetRequestDto;
import com.mtsolutions.domain.dto.request.CreatePropertyRequestDto;
import com.mtsolutions.domain.dto.request.CreateVehicleRequestDto;
import com.mtsolutions.domain.entity.Asset;
import com.mtsolutions.domain.entity.Company;
import com.mtsolutions.domain.model.PropertyDetails;
import com.mtsolutions.domain.model.VehicleDetails;
import com.mtsolutions.domain.repository.AssetRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Locale;

@ApplicationScoped
@Slf4j
public class AssetService {

    private final AssetRepository assetRepository;
    private final CompanyService companyService;
    private final AddressService addressService;
    private final DateComponent dateComponent;

    public AssetService(AssetRepository assetRepository,
                        CompanyService companyService,
                        AddressService addressService,
                        DateComponent dateComponent) {
        this.assetRepository = assetRepository;
        this.companyService = companyService;
        this.addressService = addressService;
        this.dateComponent = dateComponent;
    }

    @Transactional
    public Asset createAsset(CreateAssetRequestDto request) throws RentlixApiException {
        Company company = this.companyService.requireAdminCompany(request.companyId(), Features.CREATE_ASSET);
        if (company.getStatus() != CompanyStatus.ACTIVE) {
            throw new CompanyStatusConflictException(Features.CREATE_ASSET);
        }

        log.info("Creating asset {} for company {}", request.name(), company.getCompanyId());

        AssetDetails assetDetails = this.validateAssetDetails(request);

        LocalDateTime now = this.dateComponent.now();
        Asset asset = Asset.builder()
                .company(company)
                .type(request.type())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .pricePeriod(request.pricePeriod())
                .vehicle(assetDetails.vehicle())
                .property(assetDetails.property())
                .status(AssetStatus.AVAILABLE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        this.assetRepository.persist(asset);
        log.info("Asset created with ID: {}", asset.getAssetId());
        return asset;
    }

    protected VehicleDetails vehicle(CreateVehicleRequestDto request) {
        return VehicleDetails.builder()
                .brand(request.brand())
                .model(request.model())
                .manufactureYear(request.manufactureYear())
                .modelYear(request.modelYear())
                .color(request.color())
                .plate(this.identifier(request.plate()))
                .renavam(this.identifier(request.renavam()))
                .chassis(this.identifier(request.chassis()))
                .engineCc(request.engineCc())
                .mileage(request.mileage())
                .build();
    }

    protected PropertyDetails property(CreatePropertyRequestDto request) {
        return PropertyDetails.builder()
                .address(this.addressService.getAddress(request.address()))
                .totalArea(request.totalArea())
                .bedrooms(request.bedrooms())
                .suites(request.suites())
                .bathrooms(request.bathrooms())
                .parkingSpaces(request.parkingSpaces())
                .furnished(request.furnished())
                .petFriendly(request.petFriendly())
                .hasElevator(request.hasElevator())
                .hasPool(request.hasPool())
                .hasGym(request.hasGym())
                .hasBarbecue(request.hasBarbecue())
                .condoFee(request.condoFee())
                .iptu(request.iptu())
                .registrationNumber(request.registrationNumber())
                .build();
    }

    protected void ensureVehicleIsUnique(VehicleDetails vehicle) throws RentlixApiException {
        boolean plateExists = this.assetRepository.existsByPlate(vehicle.getPlate());
        boolean renavamExists = vehicle.getRenavam() != null && this.assetRepository.existsByRenavam(vehicle.getRenavam());
        boolean chassisExists = vehicle.getChassis() != null && this.assetRepository.existsByChassis(vehicle.getChassis());
        if (plateExists || renavamExists || chassisExists) {
            throw new AssetAlreadyExistsException(Features.CREATE_ASSET);
        }
    }

    private String identifier(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private AssetDetails validateAssetDetails(CreateAssetRequestDto request) throws RentlixApiException {
        if (request.type().getCategory() == AssetCategory.VEHICLE) {
            if (request.vehicle() == null) {
                throw new InvalidAssetDetailsException(Features.CREATE_ASSET);
            }
            VehicleDetails vehicle = this.vehicle(request.vehicle());
            this.ensureVehicleIsUnique(vehicle);
            return new AssetDetails(vehicle, null);
        }
        if (request.property() == null) {
            throw new InvalidAssetDetailsException(Features.CREATE_ASSET);
        }
        return new AssetDetails(null, this.property(request.property()));
    }

}
