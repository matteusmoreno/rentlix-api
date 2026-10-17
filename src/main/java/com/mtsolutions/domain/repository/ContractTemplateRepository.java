package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.constant.AssetCategory;
import com.mtsolutions.domain.entity.ContractTemplate;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ContractTemplateRepository implements PanacheRepositoryBase<ContractTemplate, UUID> {

    public Optional<ContractTemplate> findByCompanyAndCategory(UUID companyId, AssetCategory category) {
        return find("from ContractTemplate template join fetch template.company where template.company.companyId = ?1 and template.category = ?2", companyId, category)
                .firstResultOptional();
    }
}
