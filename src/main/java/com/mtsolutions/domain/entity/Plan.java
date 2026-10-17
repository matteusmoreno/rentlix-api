package com.mtsolutions.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "plans")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Plan {

    @Id
    @GeneratedValue
    @Column(name = "plan_id")
    private UUID planId;
    private String name;
    @Column(precision = 12, scale = 2)
    private BigDecimal price;
    private Boolean fixed;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
