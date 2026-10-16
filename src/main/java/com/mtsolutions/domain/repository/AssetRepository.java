package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.entity.Asset;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
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

    public List<Asset> findPageByCompany(UUID companyId, int page, int size) {
        return find("from Asset asset join fetch asset.company where asset.company.companyId = ?1 order by asset.createdAt desc", companyId)
                .page(page, size)
                .list();
    }

    public long countByCompany(UUID companyId) {
        return count("company.companyId", companyId);
    }
}
