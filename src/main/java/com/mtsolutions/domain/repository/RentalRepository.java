package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.constant.RentalStatus;
import com.mtsolutions.domain.entity.Rental;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RentalRepository implements PanacheRepositoryBase<Rental, UUID> {

    public Optional<Rental> findByIdWithAssetAndLessee(UUID rentalId) {
        return find("select rental from Rental rental join fetch rental.asset asset join fetch asset.company join fetch rental.lessee where rental.rentalId = ?1", rentalId)
                .firstResultOptional();
    }

    public boolean existsByAssetIdAndStatus(UUID assetId, RentalStatus status) {
        return count("asset.assetId = ?1 and status = ?2", assetId, status) > 0;
    }

    public List<Rental> findPageByCompany(UUID companyId, int page, int size) {
        return find("from Rental rental join fetch rental.asset asset join fetch asset.company join fetch rental.lessee where asset.company.companyId = ?1 order by rental.createdAt desc", companyId)
                .page(page, size)
                .list();
    }

    public long countByCompany(UUID companyId) {
        return count("asset.company.companyId = ?1", companyId);
    }

    public List<Rental> findPageByCompanyAndLessee(UUID companyId, UUID lesseeId, int page, int size) {
        return find("from Rental rental join fetch rental.asset asset join fetch asset.company join fetch rental.lessee where asset.company.companyId = ?1 and rental.lessee.userId = ?2 order by rental.createdAt desc", companyId, lesseeId)
                .page(page, size)
                .list();
    }

    public long countByCompanyAndLessee(UUID companyId, UUID lesseeId) {
        return count("asset.company.companyId = ?1 and lessee.userId = ?2", companyId, lesseeId);
    }
}
