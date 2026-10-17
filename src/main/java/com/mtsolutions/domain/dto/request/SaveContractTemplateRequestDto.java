package com.mtsolutions.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SaveContractTemplateRequestDto(
        @NotBlank(message = "Content cannot be blank")
        @Size(max = 50000, message = "Content must have at most 50000 characters")
        String content
) {}
