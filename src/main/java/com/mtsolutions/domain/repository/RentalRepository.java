package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.constant.RentalStatus;
import com.mtsolutions.domain.entity.Rental;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

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
}
