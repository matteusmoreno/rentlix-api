package com.mtsolutions.domain.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePropertyRequestDto(
        @NotNull(message = "Address cannot be null")
        @Valid
        CreateAddressRequestDto address,
        Integer totalArea,
        Integer bedrooms,
        Integer suites,
        Integer bathrooms,
        Integer parkingSpaces,
        Boolean furnished,
        Boolean petFriendly,
        Boolean hasElevator,
        Boolean hasPool,
        Boolean hasGym,
        Boolean hasBarbecue,
        BigDecimal condoFee,
        BigDecimal iptu,
        String registrationNumber
) {}
