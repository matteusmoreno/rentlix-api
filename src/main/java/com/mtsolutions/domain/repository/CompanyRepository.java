package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.entity.Company;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CompanyRepository implements PanacheRepositoryBase<Company, UUID> {

    public boolean existsByCnpj(String cnpj) {
        return count("cnpj", cnpj) > 0;
    }

    public boolean existsBySlug(String slug) {
        return count("slug", slug) > 0;
    }

    public Optional<Company> findBySlug(String slug) {
        return find("slug", slug).firstResultOptional();
    }
}
