package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.model.UserPage;

import java.util.List;

public record UserPageResponseDto(
        List<UserResponseDto> users,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public UserPageResponseDto(UserPage userPage) {
        this(
                userPage.users().stream().map(UserResponseDto::new).toList(),
                userPage.page(),
                userPage.size(),
                userPage.totalElements(),
                userPage.totalPages()
        );
    }
}
