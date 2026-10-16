package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.application.exception.AssetAlreadyExistsException;
import com.mtsolutions.application.exception.AssetNotFoundException;
import com.mtsolutions.application.exception.AssetStatusConflictException;
import com.mtsolutions.application.exception.CompanyStatusConflictException;
import com.mtsolutions.application.exception.GalleryImageNotFoundException;
import com.mtsolutions.application.exception.InvalidAssetDetailsException;
import com.mtsolutions.application.exception.PictureNotFoundException;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.constant.AssetCategory;
import com.mtsolutions.domain.constant.AssetStatus;
import com.mtsolutions.domain.constant.AssetType;
import com.mtsolutions.domain.constant.CloudinaryFolder;
import com.mtsolutions.domain.constant.CompanyStatus;
import com.mtsolutions.domain.dto.AssetDetails;
import com.mtsolutions.domain.dto.request.CreateAssetRequestDto;
import com.mtsolutions.domain.dto.request.CreatePropertyRequestDto;
import com.mtsolutions.domain.dto.request.CreateVehicleRequestDto;
import com.mtsolutions.domain.dto.request.UpdateAssetRequestDto;
import com.mtsolutions.domain.entity.Asset;
import com.mtsolutions.domain.entity.Company;
import com.mtsolutions.domain.model.AssetPage;
import com.mtsolutions.domain.model.PropertyDetails;
import com.mtsolutions.domain.model.VehicleDetails;
import com.mtsolutions.domain.repository.AssetRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class AssetService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

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
    public Asset updateAsset(UpdateAssetRequestDto request, UUID assetId) throws RentlixApiException {
        Asset asset = this.requireAsset(assetId, Features.UPDATE_ASSET);
        log.info("Updating asset {}", assetId);

        if (request.name() != null) asset.setName(request.name());
        if (request.description() != null) asset.setDescription(request.description());
        if (request.price() != null) asset.setPrice(request.price());
        if (request.pricePeriod() != null) asset.setPricePeriod(request.pricePeriod());
        if (request.vehicle() != null) {
            if (asset.getType().getCategory() != AssetCategory.VEHICLE) {
                throw new InvalidAssetDetailsException(Features.UPDATE_ASSET);
            }
            VehicleDetails vehicle = this.vehicle(request.vehicle());
            this.ensureVehicleIsUnique(assetId, vehicle, Features.UPDATE_ASSET);
            asset.setVehicle(vehicle);
        }
        if (request.property() != null) {
            if (asset.getType().getCategory() == AssetCategory.VEHICLE) {
                throw new InvalidAssetDetailsException(Features.UPDATE_ASSET);
            }
            asset.setProperty(this.property(request.property()));
        }
        asset.setUpdatedAt(this.dateComponent.now());
        this.assetRepository.persist(asset);

        log.info("Asset updated: {}", asset.getAssetId());
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
    public AssetPage listAssets(UUID companyId, int page, int size) throws RentlixApiException {
        this.companyService.requireMemberCompany(companyId, Features.LIST_ASSETS);
        PageWindow window = this.pageWindow(page, size);
        List<Asset> assets = this.assetRepository.findPageByCompany(companyId, window.page(), window.size());
        long totalElements = this.assetRepository.countByCompany(companyId);
        return new AssetPage(assets, window.page(), window.size(), totalElements);
    }

    @Transactional
    public AssetPage listPublicAssets(int page, int size) {
        PageWindow window = this.pageWindow(page, size);
        List<Asset> assets = this.assetRepository.findPublicPage(window.page(), window.size());
        long totalElements = this.assetRepository.countPublic();
        return new AssetPage(assets, window.page(), window.size(), totalElements);
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

    @Transactional
    public Asset rentAsset(UUID assetId) throws RentlixApiException {
        return this.changeStatus(assetId, AssetStatus.RENTED, Features.RENT_ASSET);
    }

    @Transactional
    public Asset startMaintenance(UUID assetId) throws RentlixApiException {
        return this.changeStatus(assetId, AssetStatus.MAINTENANCE, Features.START_ASSET_MAINTENANCE);
    }

    @Transactional
    public Asset inactivateAsset(UUID assetId) throws RentlixApiException {
        return this.changeStatus(assetId, AssetStatus.INACTIVE, Features.INACTIVATE_ASSET);
    }

    @Transactional
    public Asset activateAsset(UUID assetId) throws RentlixApiException {
        return this.changeStatus(assetId, AssetStatus.AVAILABLE, Features.ACTIVATE_ASSET);
    }

    private Asset changeStatus(UUID assetId, AssetStatus nextStatus, Features feature) throws RentlixApiException {
        Asset asset = this.requireAsset(assetId, feature);
        this.validatePreviousStatus(asset, nextStatus, feature);
        asset.setStatus(nextStatus);
        asset.setUpdatedAt(this.dateComponent.now());
        this.assetRepository.persist(asset);
        log.info("Asset {} status changed to {}", asset.getAssetId(), nextStatus);
        return asset;
    }

    protected void validatePreviousStatus(Asset asset, AssetStatus nextStatus, Features feature) throws RentlixApiException {
        if (!nextStatus.getValidPreviousStatuses().contains(asset.getStatus())) {
            throw new AssetStatusConflictException(feature);
        }
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

    protected void ensureVehicleIsUnique(UUID assetId, VehicleDetails vehicle, Features feature) throws RentlixApiException {
        boolean plateExists;
        boolean renavamExists;
        boolean chassisExists;
        if (assetId == null) {
            plateExists = this.assetRepository.existsByPlate(vehicle.getPlate());
            renavamExists = vehicle.getRenavam() != null && this.assetRepository.existsByRenavam(vehicle.getRenavam());
            chassisExists = vehicle.getChassis() != null && this.assetRepository.existsByChassis(vehicle.getChassis());
        } else {
            plateExists = this.assetRepository.existsOtherByPlate(assetId, vehicle.getPlate());
            renavamExists = vehicle.getRenavam() != null && this.assetRepository.existsOtherByRenavam(assetId, vehicle.getRenavam());
            chassisExists = vehicle.getChassis() != null && this.assetRepository.existsOtherByChassis(assetId, vehicle.getChassis());
        }
        if (plateExists || renavamExists || chassisExists) {
            throw new AssetAlreadyExistsException(feature);
        }
    }

    private String identifier(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private AssetDetails validateAssetDetails(CreateAssetRequestDto request) throws RentlixApiException {
        return this.assetDetails(request.type(), request.vehicle(), request.property(), null, Features.CREATE_ASSET);
    }

    private AssetDetails assetDetails(AssetType type,
                                      CreateVehicleRequestDto vehicleRequest,
                                      CreatePropertyRequestDto propertyRequest,
                                      UUID assetId,
                                      Features feature) throws RentlixApiException {
        if (type.getCategory() == AssetCategory.VEHICLE) {
            if (vehicleRequest == null) {
                throw new InvalidAssetDetailsException(feature);
            }
            VehicleDetails vehicle = this.vehicle(vehicleRequest);
            this.ensureVehicleIsUnique(assetId, vehicle, feature);
            return new AssetDetails(vehicle, null);
        }
        if (propertyRequest == null) {
            throw new InvalidAssetDetailsException(feature);
        }
        return new AssetDetails(null, this.property(propertyRequest));
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
