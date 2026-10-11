package com.mtsolutions.domain.dto.request;

import com.mtsolutions.domain.constant.MaritalStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UpdateUserRequestDto(
        @NotBlank(message = "Name cannot be blank")
        String name,
        LocalDate birthDate,
        @Pattern(regexp = "\\(\\d{2}\\)\\d{5}-\\d{4}", message = "Phone must be in the format (XX)XXXXX-XXXX")
        String phone,
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF must follow the format XXX.XXX.XXX-XX")
        String cpf,
        @NotBlank(message = "RG cannot be blank")
        String rg,
        String occupation,
        MaritalStatus maritalStatus,
        @Valid
        CreateAddressRequestDto address
) {}
