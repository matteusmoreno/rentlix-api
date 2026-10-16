package com.mtsolutions.domain.dto.request;

import com.mtsolutions.domain.constant.PricePeriod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateAssetRequestDto(
        @Pattern(regexp = ".*\\S.*", message = "Name cannot be blank")
        String name,
        @Pattern(regexp = ".*\\S.*", message = "Description cannot be blank")
        @Size(max = 1000, message = "Description must have at most 1000 characters")
        String description,
        @Positive(message = "Price must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 digits and 2 decimal places")
        BigDecimal price,
        PricePeriod pricePeriod,
        @Valid
        CreateVehicleRequestDto vehicle,
        @Valid
        CreatePropertyRequestDto property
) {}
