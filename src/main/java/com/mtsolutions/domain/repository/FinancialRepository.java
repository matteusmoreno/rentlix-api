package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.entity.Financial;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class FinancialRepository implements PanacheRepositoryBase<Financial, UUID> {
}
