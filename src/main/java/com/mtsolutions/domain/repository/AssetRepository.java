package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.entity.Asset;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class AssetRepository implements PanacheRepositoryBase<Asset, UUID> {

    public boolean existsByPlate(String plate) {
        return count("vehicle.plate", plate) > 0;
    }

    public boolean existsByRenavam(String renavam) {
        return count("vehicle.renavam", renavam) > 0;
    }

    public boolean existsByChassis(String chassis) {
        return count("vehicle.chassis", chassis) > 0;
    }
}
