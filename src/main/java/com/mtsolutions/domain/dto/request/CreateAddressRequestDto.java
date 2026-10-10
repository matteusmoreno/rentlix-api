package com.mtsolutions.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateAddressRequestDto(

        @NotBlank(message = "CEP cannot be blank")
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP must follow the format 12345-678 or 12345678")
        String cep,
        String street,
        String neighborhood,
        String city,
        String state,
        String number,
        String complement
) {}
