package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.model.Address;
import com.mtsolutions.domain.model.PropertyDetails;

import java.math.BigDecimal;

public record PublicPropertyResponseDto(
        String neighborhood,
        String city,
        String state,
        Integer totalArea,
        Integer bedrooms,
        Integer suites,
        Integer bathrooms,
        Integer parkingSpaces,
        Boolean furnished,
        Boolean petFriendly,
        Boolean hasElevator,
        Boolean hasPool,
        Boolean hasGym,
        Boolean hasBarbecue,
        BigDecimal condoFee,
        BigDecimal iptu
) {
    public PublicPropertyResponseDto(PropertyDetails property) {
        this(
                address(property).neighborhood(),
                address(property).city(),
                address(property).state(),
                property.getTotalArea(),
                property.getBedrooms(),
                property.getSuites(),
                property.getBathrooms(),
                property.getParkingSpaces(),
                property.getFurnished(),
                property.getPetFriendly(),
                property.getHasElevator(),
                property.getHasPool(),
                property.getHasGym(),
                property.getHasBarbecue(),
                property.getCondoFee(),
                property.getIptu()
        );
    }

    private static Location address(PropertyDetails property) {
        Address address = property.getAddress();
        if (address == null) {
            return new Location(null, null, null);
        }
        return new Location(address.getNeighborhood(), address.getCity(), address.getState());
    }

    private record Location(String neighborhood, String city, String state) {}
}
