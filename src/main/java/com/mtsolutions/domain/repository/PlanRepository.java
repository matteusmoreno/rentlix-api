package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.entity.Plan;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PlanRepository implements PanacheRepositoryBase<Plan, UUID> {

    public Optional<Plan> findFixed() {
        return find("fixed", true).firstResultOptional();
    }

    public boolean existsByName(String name) {
        return count("lower(name) = ?1", name.toLowerCase(Locale.ROOT)) > 0;
    }

    public boolean existsOtherByName(UUID planId, String name) {
        return count("planId <> ?1 and lower(name) = ?2", planId, name.toLowerCase(Locale.ROOT)) > 0;
    }

    public List<Plan> listAllByPrice() {
        return list("order by price asc, name asc");
    }
}
