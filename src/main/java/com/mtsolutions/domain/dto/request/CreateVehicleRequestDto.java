package com.mtsolutions.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateVehicleRequestDto(
        @NotBlank(message = "Brand cannot be blank")
        String brand,
        @NotBlank(message = "Model cannot be blank")
        String model,
        Integer manufactureYear,
        Integer modelYear,
        String color,
        @NotBlank(message = "Plate cannot be blank")
        String plate,
        @Size(max = 11, message = "Renavam must have at most 11 characters")
        String renavam,
        @Size(max = 17, message = "Chassis must have at most 17 characters")
        String chassis,
        Integer engineCc,
        Integer mileage
) {}
