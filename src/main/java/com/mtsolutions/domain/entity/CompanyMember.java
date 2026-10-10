package com.mtsolutions.domain.entity;

import com.mtsolutions.domain.constant.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company_members")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class CompanyMember {

    @EmbeddedId
    private CompanyMemberId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("companyId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 90)
    private UserRole role;
}
