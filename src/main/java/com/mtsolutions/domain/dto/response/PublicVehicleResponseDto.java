package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.model.VehicleDetails;

public record PublicVehicleResponseDto(
        String brand,
        String model,
        Integer manufactureYear,
        Integer modelYear,
        String color,
        Integer engineCc,
        Integer mileage
) {
    public PublicVehicleResponseDto(VehicleDetails vehicle) {
        this(
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getManufactureYear(),
                vehicle.getModelYear(),
                vehicle.getColor(),
                vehicle.getEngineCc(),
                vehicle.getMileage()
        );
    }
}
