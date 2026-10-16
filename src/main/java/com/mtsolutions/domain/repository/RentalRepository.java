package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.entity.Rental;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class RentalRepository implements PanacheRepositoryBase<Rental, UUID> {
}
