package com.mtsolutions.domain.dto;

import com.mtsolutions.domain.model.PropertyDetails;
import com.mtsolutions.domain.model.VehicleDetails;

public record AssetDetails(
        VehicleDetails vehicle,
        PropertyDetails property) {}
