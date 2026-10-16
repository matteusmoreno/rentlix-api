package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.model.AssetPage;

import java.util.List;

public record AssetPageResponseDto(
        List<AssetResponseDto> assets,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public AssetPageResponseDto(AssetPage assetPage) {
        this(
                assetPage.assets().stream().map(AssetResponseDto::new).toList(),
                assetPage.page(),
                assetPage.size(),
                assetPage.totalElements(),
                assetPage.totalPages()
        );
    }
}
