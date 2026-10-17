package com.mtsolutions.domain.dto.request;

import com.mtsolutions.domain.constant.FinancialDirection;
import com.mtsolutions.domain.constant.FinancialType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateFinancialRequestDto(
        FinancialDirection direction,
        FinancialType type,
        @Positive(message = "Amount must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 digits and 2 decimal places")
        BigDecimal amount,
        @Pattern(regexp = ".*\\S.*", message = "Description cannot be blank")
        @Size(max = 255, message = "Description must have at most 255 characters")
        String description,
        LocalDate occurredOn
) {}
