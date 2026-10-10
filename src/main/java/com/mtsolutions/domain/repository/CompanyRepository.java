package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.entity.Company;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class CompanyRepository implements PanacheRepositoryBase<Company, UUID> {
}
