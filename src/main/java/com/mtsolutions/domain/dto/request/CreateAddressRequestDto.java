package com.mtsolutions.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateAddressRequestDto(

        @NotBlank(message = "CEP cannot be blank")
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP must follow the format 12345-678 or 12345678")
        String cep,
        @NotBlank(message = "Street cannot be blank")
        String street,
        @NotBlank(message = "Neighborhood cannot be blank")
        String neighborhood,
        @NotBlank(message = "City cannot be blank")
        String city,
        @NotBlank(message = "State cannot be blank")
        String state,
        String number,
        String complement
) {}
