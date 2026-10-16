package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.entity.CompanyMember;
import com.mtsolutions.domain.entity.CompanyMemberId;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CompanyMemberRepository implements PanacheRepositoryBase<CompanyMember, CompanyMemberId> {

    public List<CompanyMember> findByUserId(UUID userId) {
        return find("select member from CompanyMember member join fetch member.company where member.user.userId = ?1 order by member.company.createdAt desc", userId).list();
    }
}
