package com.mtsolutions.domain.dto.request;

import com.mtsolutions.domain.constant.UserRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddCompanyMemberRequestDto(
        @NotNull(message = "User id cannot be null")
        UUID userId,
        @NotNull(message = "Role cannot be null")
        UserRole role
) {}
