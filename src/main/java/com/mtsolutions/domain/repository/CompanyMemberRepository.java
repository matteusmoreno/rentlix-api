package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.entity.CompanyMember;
import com.mtsolutions.domain.entity.CompanyMemberId;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CompanyMemberRepository implements PanacheRepositoryBase<CompanyMember, CompanyMemberId> {

    public Optional<CompanyMember> findByUserIdAndCompanyId(UUID userId, UUID companyId) {
        return find("user.userId = ?1 and company.companyId = ?2", userId, companyId).firstResultOptional();
    }

    public boolean existsByUserIdAndCompanyId(UUID userId, UUID companyId) {
        return count("user.userId = ?1 and company.companyId = ?2", userId, companyId) > 0;
    }

    public List<CompanyMember> findByCompanyId(UUID companyId) {
        return find("select member from CompanyMember member join fetch member.user where member.company.companyId = ?1 order by member.user.name", companyId).list();
    }

    public List<CompanyMember> findByUserId(UUID userId) {
        return find("select member from CompanyMember member join fetch member.company where member.user.userId = ?1 order by member.company.createdAt desc", userId).list();
    }
}
