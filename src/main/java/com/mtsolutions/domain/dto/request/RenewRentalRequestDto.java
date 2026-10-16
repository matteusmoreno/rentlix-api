package com.mtsolutions.domain.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RenewRentalRequestDto(
        @NotNull(message = "End date cannot be null")
        LocalDate endDate
) {}
