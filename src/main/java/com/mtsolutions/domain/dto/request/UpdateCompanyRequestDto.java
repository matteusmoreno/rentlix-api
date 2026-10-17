package com.mtsolutions.domain.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCompanyRequestDto(
        @Pattern(regexp = ".*\\S.*", message = "Name cannot be blank")
        @Size(max = 150, message = "Name must have at most 150 characters")
        String name,
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must contain only lowercase letters, numbers and hyphens")
        @Size(max = 160, message = "Slug must have at most 160 characters")
        String slug,
        @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "CNPJ must follow the format XX.XXX.XXX/XXXX-XX")
        String cnpj
) {}
