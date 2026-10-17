package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.constant.FinancialDirection;
import com.mtsolutions.domain.entity.Financial;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class FinancialRepository implements PanacheRepositoryBase<Financial, UUID> {

    public Optional<Financial> findByIdWithReferences(UUID financialId) {
        return find("select financial from Financial financial join fetch financial.company left join fetch financial.asset left join fetch financial.rental where financial.financialId = ?1", financialId)
                .firstResultOptional();
    }

    public List<Financial> findPage(UUID companyId, UUID assetId, LocalDate from, LocalDate to, int page, int size) {
        Map<String, Object> params = new HashMap<>();
        String where = this.where(companyId, assetId, from, to, params, true);
        String query = "from Financial financial join fetch financial.company left join fetch financial.asset left join fetch financial.rental where "
                + where
                + " order by financial.occurredOn desc, financial.createdAt desc";
        return find(query, params).page(page, size).list();
    }

    public long count(UUID companyId, UUID assetId, LocalDate from, LocalDate to) {
        Map<String, Object> params = new HashMap<>();
        return count(this.where(companyId, assetId, from, to, params, false), params);
    }

    public Map<FinancialDirection, BigDecimal> sumByDirection(UUID companyId, UUID assetId, LocalDate from, LocalDate to) {
        Map<String, Object> params = new HashMap<>();
        String where = this.where(companyId, assetId, from, to, params, true);
        var query = getEntityManager().createQuery(
                "select financial.direction, sum(financial.amount) from Financial financial where " + where + " group by financial.direction",
                Object[].class);
        params.forEach(query::setParameter);
        Map<FinancialDirection, BigDecimal> sums = new EnumMap<>(FinancialDirection.class);
        for (Object[] row : query.getResultList()) {
            sums.put((FinancialDirection) row[0], (BigDecimal) row[1]);
        }
        return sums;
    }

    private String where(UUID companyId, UUID assetId, LocalDate from, LocalDate to, Map<String, Object> params, boolean aliased) {
        String prefix = aliased ? "financial." : "";
        StringBuilder where = new StringBuilder(prefix).append("company.companyId = :companyId");
        params.put("companyId", companyId);
        if (assetId != null) {
            where.append(" and ").append(prefix).append("asset.assetId = :assetId");
            params.put("assetId", assetId);
        }
        if (from != null) {
            where.append(" and ").append(prefix).append("occurredOn >= :from");
            params.put("from", from);
        }
        if (to != null) {
            where.append(" and ").append(prefix).append("occurredOn <= :to");
            params.put("to", to);
        }
        return where.toString();
    }
}
