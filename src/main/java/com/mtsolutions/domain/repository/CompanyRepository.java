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

    public boolean existsOtherByCnpj(UUID companyId, String cnpj) {
        return count("companyId <> ?1 and cnpj = ?2", companyId, cnpj) > 0;
    }

    public boolean existsOtherBySlug(UUID companyId, String slug) {
        return count("companyId <> ?1 and slug = ?2", companyId, slug) > 0;
    }

    public Optional<Company> findBySlug(String slug) {
        return find("slug", slug).firstResultOptional();
    }
}
