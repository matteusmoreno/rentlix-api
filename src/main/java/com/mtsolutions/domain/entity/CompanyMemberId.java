package com.mtsolutions.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter @Setter
public class CompanyMemberId implements Serializable {

    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID userId;

    @Column(name = "company_id", insertable = false, updatable = false)
    private UUID companyId;
}
