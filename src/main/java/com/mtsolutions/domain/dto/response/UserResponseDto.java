package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.constant.MaritalStatus;
import com.mtsolutions.domain.entity.User;
import com.mtsolutions.domain.model.Address;
import com.mtsolutions.domain.model.UserDocument;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto(
        UUID userId,
        String name,
        LocalDate birthDate,
        String phone,
        String cpf,
        String rg,
        String occupation,
        MaritalStatus maritalStatus,
        Address address,
        String email,
        Boolean emailVerified,
        String pictureUrl,
        UserDocument documents,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean active,
        Boolean admin
) {
    public UserResponseDto(User user) {
        this(
                user.getUserId(),
                user.getName(),
                user.getBirthDate(),
                user.getPhone(),
                user.getCpf(),
                user.getRg(),
                user.getOccupation(),
                user.getMaritalStatus(),
                user.getAddress(),
                user.getEmail(),
                user.getEmailVerified(),
                user.getPictureUrl(),
                user.getDocuments(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getActive(),
                user.getAdmin()
        );
    }
}
