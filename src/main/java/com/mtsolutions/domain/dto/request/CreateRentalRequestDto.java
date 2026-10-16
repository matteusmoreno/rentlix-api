package com.mtsolutions.domain.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateRentalRequestDto(
        @NotNull(message = "Asset id cannot be null")
        UUID assetId,
        @NotNull(message = "Lessee id cannot be null")
        UUID lesseeId,
        @NotNull(message = "Start date cannot be null")
        LocalDate startDate,
        @NotNull(message = "End date cannot be null")
        LocalDate endDate
) {}
