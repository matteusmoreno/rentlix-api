package com.mtsolutions.domain.dto.request;

import com.mtsolutions.domain.constant.AssetType;
import com.mtsolutions.domain.constant.PricePeriod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAssetRequestDto(
        @NotNull(message = "Company id cannot be null")
        UUID companyId,
        @NotNull(message = "Type cannot be null")
        AssetType type,
        @NotBlank(message = "Name cannot be blank")
        String name,
        @Size(max = 1000, message = "Description must have at most 1000 characters")
        String description,
        @NotNull(message = "Price cannot be null")
        @Positive(message = "Price must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 digits and 2 decimal places")
        BigDecimal price,
        @NotNull(message = "Price period cannot be null")
        PricePeriod pricePeriod,
        @Valid
        CreateVehicleRequestDto vehicle,
        @Valid
        CreatePropertyRequestDto property
) {}
