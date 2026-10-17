package com.mtsolutions.domain.dto.request;

import com.mtsolutions.domain.constant.FinancialType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateFinancialRequestDto(
        @NotNull(message = "Company id cannot be null")
        UUID companyId,
        UUID assetId,
        UUID rentalId,
        @NotNull(message = "Type cannot be null")
        FinancialType type,
        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 digits and 2 decimal places")
        BigDecimal amount,
        @NotBlank(message = "Description cannot be blank")
        @Size(max = 255, message = "Description must have at most 255 characters")
        String description,
        @NotNull(message = "Occurred on cannot be null")
        LocalDate occurredOn
) {}
