package com.mtsolutions.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Address {

    private String zipCode;
    private String street;
    private String neighborhood;
    private String city;
    private String state;
    private String number;
    private String complement;

}
