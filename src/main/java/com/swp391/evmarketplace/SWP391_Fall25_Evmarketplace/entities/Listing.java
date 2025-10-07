package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Visibility;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "listing")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_listing_category")
    )
    private Category category;

    @Column(length = 255)
    private String title;


    @ManyToOne
    @JoinColumn(name = "product_vehicle_id",
            foreignKey = @ForeignKey(name = "fk_listing_vehicle"))
    private ProductVehicle productVehicle;

    @ManyToOne
    @JoinColumn(name = "product_battery_id",
            foreignKey = @ForeignKey(name = "fk_listing_battery"))
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

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "model", length = 100, nullable = false)
    private String model;

    @Column(name = "model_id")
    private Long modelId;

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
    private ListingStatus status = ListingStatus.PENDING;

    @Column(name = "province", length = 100)
    private String province;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "ward", length = 100)
    private String ward;

    @Column(name = "address", length = 300)
    private String address;


    @Column(name = "is_consigned", nullable = false)
    private Boolean consigned = Boolean.FALSE;

    @Column(name = "promoted_until")
    private LocalDateTime promotedUntil;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "listing", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
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
