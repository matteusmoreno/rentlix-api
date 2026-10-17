package com.mtsolutions.domain.entity;

import com.mtsolutions.domain.constant.CompanyPlan;
import com.mtsolutions.domain.constant.CompanyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "companies")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Company {

    @Id
    @GeneratedValue
    @Column(name = "company_id")
    private UUID companyId;
    private String name;
    private String slug;
    @Column(name = "logo_url")
    private String logoUrl;
    private String cnpj;

    @Enumerated(EnumType.STRING)
    private CompanyPlan plan;

    @Enumerated(EnumType.STRING)
    private CompanyStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
