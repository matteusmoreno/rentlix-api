package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.model.RentalPage;

import java.util.List;

public record RentalPageResponseDto(
        List<RentalResponseDto> rentals,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public RentalPageResponseDto(RentalPage rentalPage) {
        this(
                rentalPage.rentals().stream().map(RentalResponseDto::new).toList(),
                rentalPage.page(),
                rentalPage.size(),
                rentalPage.totalElements(),
                rentalPage.totalPages()
        );
    }
}
