package com.mtsolutions.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class VehicleDetails {

    private String brand;
    private String model;
    @Column(name = "manufacture_year")
    private Integer manufactureYear;
    @Column(name = "model_year")
    private Integer modelYear;
    private String color;
    private String plate;
    private String renavam;
    private String chassis;
    @Column(name = "engine_cc")
    private Integer engineCc;
    private Integer mileage;
}
