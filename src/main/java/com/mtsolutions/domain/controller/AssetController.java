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

    public Asset uploadImage(UUID assetId, byte[] image) throws RentlixApiException {
        return this.assetService.uploadImage(assetId, image);
    }

    public Asset deleteImage(UUID assetId) throws RentlixApiException {
        return this.assetService.deleteImage(assetId);
    }
}
