package com.mtsolutions.application.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ViaCepRequestDto(
        @NotBlank(message = "CEP must not be blank")
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP must follow the format 12345-678 or 12345678")
        String zipCode,
        String number,
        String complement
) {}
