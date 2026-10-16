package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.constant.AssetStatus;
import com.mtsolutions.domain.constant.CompanyStatus;
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

    public boolean existsOtherByPlate(UUID assetId, String plate) {
        return count("assetId <> ?1 and vehicle.plate = ?2", assetId, plate) > 0;
    }

    public boolean existsOtherByRenavam(UUID assetId, String renavam) {
        return count("assetId <> ?1 and vehicle.renavam = ?2", assetId, renavam) > 0;
    }

    public boolean existsOtherByChassis(UUID assetId, String chassis) {
        return count("assetId <> ?1 and vehicle.chassis = ?2", assetId, chassis) > 0;
    }

    public List<Asset> findPageByCompany(UUID companyId, int page, int size) {
        return find("from Asset asset join fetch asset.company where asset.company.companyId = ?1 order by asset.createdAt desc", companyId)
                .page(page, size)
                .list();
    }

    public long countByCompany(UUID companyId) {
        return count("company.companyId", companyId);
    }

    public List<Asset> findPublicPage(int page, int size) {
        return find("from Asset asset join fetch asset.company where asset.status = ?1 and asset.company.status = ?2 order by asset.createdAt desc", AssetStatus.AVAILABLE, CompanyStatus.ACTIVE)
                .page(page, size)
                .list();
    }

    public long countPublic() {
        return count("status = ?1 and company.status = ?2", AssetStatus.AVAILABLE, CompanyStatus.ACTIVE);
    }
}
