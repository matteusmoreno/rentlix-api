package com.mtsolutions.domain.repository;

import com.mtsolutions.domain.entity.CompanyMember;
import com.mtsolutions.domain.entity.CompanyMemberId;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CompanyMemberRepository implements PanacheRepositoryBase<CompanyMember, CompanyMemberId> {
}
