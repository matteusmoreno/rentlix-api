package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.constant.UserRole;
import com.mtsolutions.domain.entity.CompanyMember;

import java.util.UUID;

public record CompanyUserResponseDto(
        UUID userId,
        String name,
        String email,
        UserRole role
) {
    public CompanyUserResponseDto(CompanyMember member) {
        this(
                member.getUser().getUserId(),
                member.getUser().getName(),
                member.getUser().getEmail(),
                member.getRole()
        );
    }
}
