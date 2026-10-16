package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.model.AssetPage;

import java.util.List;

public record PublicAssetPageResponseDto(
        List<PublicAssetResponseDto> assets,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public PublicAssetPageResponseDto(AssetPage assetPage) {
        this(
                assetPage.assets().stream().map(PublicAssetResponseDto::new).toList(),
                assetPage.page(),
                assetPage.size(),
                assetPage.totalElements(),
                assetPage.totalPages()
        );
    }
}
