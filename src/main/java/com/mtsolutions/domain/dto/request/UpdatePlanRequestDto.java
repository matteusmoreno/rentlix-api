package com.mtsolutions.domain.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdatePlanRequestDto(
        @Pattern(regexp = ".*\\S.*", message = "Name cannot be blank")
        @Size(max = 150, message = "Name must have at most 150 characters")
        String name,
        @Positive(message = "Price must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 digits and 2 decimal places")
        BigDecimal price
) {}
