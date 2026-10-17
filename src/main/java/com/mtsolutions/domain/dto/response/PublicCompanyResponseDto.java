package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.model.PublicCompany;

import java.util.List;

public record PublicCompanyResponseDto(
        String name,
        String slug,
        String logoUrl,
        List<PublicAssetResponseDto> assets,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public PublicCompanyResponseDto(PublicCompany publicCompany) {
        this(
                publicCompany.company().getName(),
                publicCompany.company().getSlug(),
                publicCompany.company().getLogoUrl(),
                publicCompany.assets().assets().stream().map(PublicAssetResponseDto::new).toList(),
                publicCompany.assets().page(),
                publicCompany.assets().size(),
                publicCompany.assets().totalElements(),
                publicCompany.assets().totalPages()
        );
    }
}
