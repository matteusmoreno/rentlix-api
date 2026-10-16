package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.application.exception.AssetAlreadyExistsException;
import com.mtsolutions.application.exception.AssetNotFoundException;
import com.mtsolutions.application.exception.CompanyStatusConflictException;
import com.mtsolutions.application.exception.GalleryImageNotFoundException;
import com.mtsolutions.application.exception.InvalidAssetDetailsException;
import com.mtsolutions.application.exception.PictureNotFoundException;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.constant.AssetCategory;
import com.mtsolutions.domain.constant.AssetStatus;
import com.mtsolutions.domain.constant.CloudinaryFolder;
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
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class AssetService {

    private final AssetRepository assetRepository;
    private final CompanyService companyService;
    private final AddressService addressService;
    private final CloudinaryService cloudinaryService;
    private final DateComponent dateComponent;

    public AssetService(AssetRepository assetRepository,
                        CompanyService companyService,
                        AddressService addressService,
                        CloudinaryService cloudinaryService,
                        DateComponent dateComponent) {
        this.assetRepository = assetRepository;
        this.companyService = companyService;
        this.addressService = addressService;
        this.cloudinaryService = cloudinaryService;
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

    @Transactional
    public Asset getAsset(UUID assetId) throws RentlixApiException {
        Asset asset = this.assetRepository.findByIdOptional(assetId)
                .orElseThrow(() -> new AssetNotFoundException(Features.GET_ASSET));
        this.companyService.requireCompanyMember(asset.getCompany().getCompanyId(), Features.GET_ASSET);
        return asset;
    }

    @Transactional
    public Asset getPublicAsset(UUID assetId) throws RentlixApiException {
        Asset asset = this.assetRepository.findByIdOptional(assetId)
                .orElseThrow(() -> new AssetNotFoundException(Features.GET_PUBLIC_ASSET));
        if (asset.getStatus() != AssetStatus.AVAILABLE || asset.getCompany().getStatus() != CompanyStatus.ACTIVE) {
            throw new AssetNotFoundException(Features.GET_PUBLIC_ASSET);
        }
        return asset;
    }

    @Transactional
    public Asset uploadImage(UUID assetId, byte[] fileBytes) throws RentlixApiException {
        Asset asset = this.requireAsset(assetId, Features.UPLOAD_ASSET_IMAGE);

        if (asset.getImageUrl() != null && !asset.getImageUrl().isBlank()) {
            String oldPublicId = this.cloudinaryService.extractPublicId(asset.getImageUrl());
            if (oldPublicId != null) {
                this.cloudinaryService.delete(oldPublicId, CloudinaryFolder.ASSET_IMAGE.getResourceType(), Features.UPLOAD_ASSET_IMAGE);
            }
        }

        String url = this.cloudinaryService.upload(fileBytes, asset.getAssetId().toString(), CloudinaryFolder.ASSET_IMAGE, Features.UPLOAD_ASSET_IMAGE);
        asset.setImageUrl(url);
        asset.setUpdatedAt(this.dateComponent.now());
        this.assetRepository.persist(asset);

        log.info("Image uploaded for asset: {}", asset.getAssetId());
        return asset;
    }

    @Transactional
    public Asset deleteImage(UUID assetId) throws RentlixApiException {
        Asset asset = this.requireAsset(assetId, Features.DELETE_ASSET_IMAGE);

        if (asset.getImageUrl() == null || asset.getImageUrl().isBlank()) {
            throw new PictureNotFoundException(Features.DELETE_ASSET_IMAGE);
        }

        String publicId = this.cloudinaryService.extractPublicId(asset.getImageUrl());
        if (publicId != null) {
            this.cloudinaryService.delete(publicId, CloudinaryFolder.ASSET_IMAGE.getResourceType(), Features.DELETE_ASSET_IMAGE);
        }
        asset.setImageUrl(null);
        asset.setUpdatedAt(this.dateComponent.now());
        this.assetRepository.persist(asset);

        log.info("Image deleted for asset: {}", assetId);
        return asset;
    }

    @Transactional
    public Asset uploadGalleryImage(UUID assetId, byte[] fileBytes) throws RentlixApiException {
        Asset asset = this.requireAsset(assetId, Features.UPLOAD_ASSET_GALLERY);

        String publicId = UUID.randomUUID().toString();
        String url = this.cloudinaryService.upload(fileBytes, publicId, CloudinaryFolder.ASSET_GALLERY, Features.UPLOAD_ASSET_GALLERY);
        asset.getGallery().add(url);
        asset.setUpdatedAt(this.dateComponent.now());
        this.assetRepository.persist(asset);

        log.info("Gallery image uploaded for asset: {}", asset.getAssetId());
        return asset;
    }

    @Transactional
    public Asset deleteGalleryImage(UUID assetId, Integer position) throws RentlixApiException {
        Asset asset = this.requireAsset(assetId, Features.DELETE_ASSET_GALLERY);

        if (position == null || position < 0 || position >= asset.getGallery().size()) {
            throw new GalleryImageNotFoundException(Features.DELETE_ASSET_GALLERY);
        }

        String url = asset.getGallery().get(position);
        String publicId = this.cloudinaryService.extractPublicId(url);
        if (publicId != null) {
            this.cloudinaryService.delete(publicId, CloudinaryFolder.ASSET_GALLERY.getResourceType(), Features.DELETE_ASSET_GALLERY);
        }
        asset.getGallery().remove(position.intValue());
        asset.setUpdatedAt(this.dateComponent.now());
        this.assetRepository.persist(asset);

        log.info("Gallery image deleted for asset {} at position {}", assetId, position);
        return asset;
    }

    protected Asset requireAsset(UUID assetId, Features feature) throws RentlixApiException {
        Asset asset = this.assetRepository.findByIdOptional(assetId)
                .orElseThrow(() -> new AssetNotFoundException(feature));
        this.companyService.requireAdminCompany(asset.getCompany().getCompanyId(), feature);
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
