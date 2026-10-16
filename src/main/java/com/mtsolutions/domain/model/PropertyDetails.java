package com.mtsolutions.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class PropertyDetails {

    @Embedded
    private Address address;

    @Column(name = "total_area")
    private Integer totalArea;
    private Integer bedrooms;
    private Integer suites;
    private Integer bathrooms;
    @Column(name = "parking_spaces")
    private Integer parkingSpaces;

    private Boolean furnished;
    @Column(name = "pet_friendly")
    private Boolean petFriendly;
    @Column(name = "has_elevator")
    private Boolean hasElevator;
    @Column(name = "has_pool")
    private Boolean hasPool;
    @Column(name = "has_gym")
    private Boolean hasGym;
    @Column(name = "has_barbecue")
    private Boolean hasBarbecue;

    @Column(name = "condo_fee")
    private BigDecimal condoFee;
    private BigDecimal iptu;
    @Column(name = "registration_number")
    private String registrationNumber;
}
