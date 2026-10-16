package com.mtsolutions.domain.controller;

import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.dto.request.CreateAssetRequestDto;
import com.mtsolutions.domain.entity.Asset;
import com.mtsolutions.domain.service.AssetService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    public Asset createAsset(CreateAssetRequestDto request) throws RentlixApiException {
        return this.assetService.createAsset(request);
    }

    public Asset getAsset(UUID assetId) throws RentlixApiException {
        return this.assetService.getAsset(assetId);
    }

    public Asset getPublicAsset(UUID assetId) throws RentlixApiException {
        return this.assetService.getPublicAsset(assetId);
    }

    public Asset uploadImage(UUID assetId, byte[] image) throws RentlixApiException {
        return this.assetService.uploadImage(assetId, image);
    }

    public Asset deleteImage(UUID assetId) throws RentlixApiException {
        return this.assetService.deleteImage(assetId);
    }

    public Asset uploadGalleryImage(UUID assetId, byte[] image) throws RentlixApiException {
        return this.assetService.uploadGalleryImage(assetId, image);
    }

    public Asset deleteGalleryImage(UUID assetId, Integer position) throws RentlixApiException {
        return this.assetService.deleteGalleryImage(assetId, position);
    }
}
