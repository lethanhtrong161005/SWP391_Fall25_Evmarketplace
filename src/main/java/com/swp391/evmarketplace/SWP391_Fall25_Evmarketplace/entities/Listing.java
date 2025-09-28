package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Status;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.BatchSize;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "listing",
        indexes = {
                @Index(name = "idx_listing_search",  columnList = "brand,model,year,price"),
                @Index(name = "idx_listing_flags",   columnList = "verified,visibility,status"),
                @Index(name = "idx_listing_geo",     columnList = "province,city"),
                @Index(name = "idx_listing_vehicle", columnList = "product_vehicle_id"),
                @Index(name = "idx_listing_battery", columnList = "product_battery_id")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String title;

    @ManyToOne
    @JoinColumn(
            name = "product_vehicle_id",
            foreignKey = @ForeignKey(name = "fk_listing_vehicle")
    )
    private ProductVehicle productVehicle;

    @ManyToOne
    @JoinColumn(
            name = "product_battery_id",
            foreignKey = @ForeignKey(name = "fk_listing_battery")
    )
    private ProductBattery productBattery;

    @ManyToOne(optional = false)
    @JoinColumn(name = "seller_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_listing_seller"))
    private Account seller;

    @ManyToOne
    @JoinColumn(name = "branch_id",
            foreignKey = @ForeignKey(name = "fk_listing_branch"))
    private Branch branch;

    @Column(name = "brand", length = 100, nullable = false)
    private String brand;

    @Column(name = "model", length = 100, nullable = false)
    private String model;

    @Column(name = "year", columnDefinition = "smallint unsigned")
    private Integer year;

    @Column(name = "battery_capacity_kwh", precision = 6, scale = 2)
    private BigDecimal batteryCapacityKwh;

    @Column(name = "soh_percent", precision = 5, scale = 2)
    private BigDecimal sohPercent;

    @Column(name = "mileage_km")
    private Integer mileageKm;

    @Column(name = "color", length = 50)
    private String color;

    @Lob
    @Column(name = "description", columnDefinition = "text")
    private String description;

    // Giá & nhãn
    @Column(name = "price", precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "ai_suggested_price", precision = 12, scale = 2)
    private BigDecimal aiSuggestedPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 16)
    private Visibility visibility = Visibility.NORMAL;

    @Column(name = "verified", nullable = false)
    private Boolean verified = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private Status status = Status.ACTIVE;

    // Địa lý
    @Column(name = "province", length = 100)
    private String province;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "address")
    private String address;

    // Ký gửi / Promote / Hết hạn
    @Column(name = "is_consigned", nullable = false)
    private Boolean consigned = Boolean.FALSE;

    @Column(name = "promoted_until")
    private LocalDateTime promotedUntil;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Audit
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "listing", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ListingMedia> mediaList = new ArrayList<>();

    @OneToMany(mappedBy = "listing", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Favorite> favorites = new ArrayList<>();

    public void addMedia(ListingMedia media) {
        media.setListing(this);
        mediaList.add(media);
    }

}
