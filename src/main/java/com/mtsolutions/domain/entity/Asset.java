package com.mtsolutions.domain.entity;

import com.mtsolutions.domain.constant.AssetStatus;
import com.mtsolutions.domain.constant.AssetType;
import com.mtsolutions.domain.constant.PricePeriod;
import com.mtsolutions.domain.model.PropertyDetails;
import com.mtsolutions.domain.model.VehicleDetails;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "assets")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Asset {

    @Id
    @GeneratedValue
    @Column(name = "asset_id")
    private UUID assetId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    private AssetType type;
    private String name;
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "asset_gallery", joinColumns = @JoinColumn(name = "asset_id"))
    @OrderColumn(name = "position")
    @Column(name = "image_url")
    @Builder.Default
    private List<String> gallery = new ArrayList<>();

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_period")
    private PricePeriod pricePeriod;

    @Embedded
    private VehicleDetails vehicle;

    @Embedded
    private PropertyDetails property;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    private AssetStatus status;
}
