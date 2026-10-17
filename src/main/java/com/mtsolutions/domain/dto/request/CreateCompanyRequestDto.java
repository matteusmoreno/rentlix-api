package com.mtsolutions.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateCompanyRequestDto(
        @NotBlank(message = "Name cannot be blank")
        String name,
        @NotBlank(message = "CNPJ cannot be blank")
        @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "CNPJ must follow the format XX.XXX.XXX/XXXX-XX")
        String cnpj
) {}
