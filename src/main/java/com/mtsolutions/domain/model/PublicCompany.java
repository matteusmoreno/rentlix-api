package com.mtsolutions.domain.model;

import com.mtsolutions.domain.entity.Company;

public record PublicCompany(
        Company company,
        AssetPage assets
) {}
