package com.mtsolutions.domain.dto.request;

import com.mtsolutions.domain.constant.MaritalStatus;
import com.mtsolutions.domain.model.UserDocument;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record CreateUserRequestDto(
        @NotBlank(message = "Name cannot be blank")
        String name,
        @NotNull(message = "Birth date cannot be null")
        LocalDate birthDate,
        @NotBlank(message = "Phone cannot be blank")
        @Pattern(regexp = "\\(\\d{2}\\)\\d{5}-\\d{4}", message = "Phone must be in the format (XX)XXXXX-XXXX")
        String phone,
        @NotBlank(message = "CPF cannot be blank")
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF must follow the format XXX.XXX.XXX-XX")
        String cpf,
        @NotBlank(message = "RG cannot be blank")
        String rg,
        String occupation,
        @NotNull(message = "Marital status cannot be null")
        MaritalStatus maritalStatus,
        @NotNull(message = "Address cannot be null")
        CreateAddressRequestDto address,
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be a valid email address")
        String email,
        @NotBlank(message = "Password cannot be blank")
        String password,
        String pictureUrl,
        UserDocument userDocument
) {
}
