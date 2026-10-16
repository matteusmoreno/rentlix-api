package com.mtsolutions.domain.entity;

import com.mtsolutions.domain.constant.PricePeriod;
import com.mtsolutions.domain.constant.RentalStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rentals")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Rental {

    @Id
    @GeneratedValue
    @Column(name = "rental_id")
    private UUID rentalId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lessee_id", nullable = false)
    private User lessee;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_period")
    private PricePeriod pricePeriod;

    @Column(name = "contract_url")
    private String contractUrl;

    @Enumerated(EnumType.STRING)
    private RentalStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
