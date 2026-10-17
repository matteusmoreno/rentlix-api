package com.mtsolutions.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequestDto(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "Code is required")
        String code,
        @NotBlank(message = "Password is required")
        String password
) {}
