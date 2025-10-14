package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Visibility;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.BrandRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.CategoryRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ModelRepository;
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

    @Column(name = "rejected_reason", length = 255)
    private String rejectedReason;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

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

    @Column(name = "voltage_v")
    private BigDecimal voltage;

    @Column(name = "battery_chemistry")
    private String batteryChemistry;

    @Column(name = "mass_kg")
    private BigDecimal massKg;

    @Column(name = "dimensions_mm")
    private String dimensions;

    @Column(name = "hidden_at")
    private LocalDateTime hiddenAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "prev_status")
    @Enumerated(EnumType.STRING)
    private ListingStatus prevStatus;

    @Column(name = "prev_visibility")
    @Enumerated(EnumType.STRING)
    private Visibility prevVisibility;

    @Column(name = "prev_expires_at")
    private LocalDateTime prevExpiresAt;


    public void addMedia(ListingMedia media) {
        media.setListing(this);
        mediaList.add(media);
    }

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<ListingStatusHistory> statusHistories = new ArrayList<>();

    public void addHistory(ListingStatusHistory h) {
        h.setListing(this);
        this.statusHistories.add(h);
    }

    public ListingDto toDto(Listing listing, BrandRepository brandRepository, CategoryRepository categoryRepository, ModelRepository modelRepository) {
        ListingDto listingDto = new ListingDto();

        listingDto.setId(listing.getId());

        listingDto.setCategoryId(listing.getCategory().getId());
        listingDto.setCategoryName(listing.getCategory().getName());

        if(listing.getBrandId() != null){
            listingDto.setBrandId(listing.getBrandId());
            Brand b = brandRepository.findById(listing.getBrandId()).orElseThrow(() -> new CustomBusinessException("Brand not found"));
            listingDto.setBrand(b.getName());
        }else{
            listingDto.setBrand(listing.getBrand());
        }

        if(listing.getModelId() != null){
            listingDto.setModelId(listing.getModelId());
            Model m = modelRepository.findById(listing.getModelId()).orElse(null);
            listingDto.setModel(m.getName());
        }else{
            listingDto.setModel(listing.getModel());
        }

        listingDto.setSellerId(listing.getSeller().getId());

        listingDto.setTitle(listing.getTitle());
        listingDto.setDescription(listing.getDescription());
        listingDto.setPrice(listing.getPrice());

        listingDto.setStatus(listing.getStatus());
        listingDto.setYear(listing.getYear());
        listingDto.setAiSuggestedPrice(listing.getAiSuggestedPrice());
        listingDto.setMileageKm(listing.getMileageKm());
        listingDto.setBatteryCapacityKwh(listing.getBatteryCapacityKwh());
        listingDto.setSohPercent(listing.getSohPercent());
        listingDto.setColor(listing.getColor());
        listingDto.setIsConsigned(listing.getConsigned());

        listingDto.setProvince(listing.getProvince());
        listingDto.setDistrict(listing.getDistrict());
        listingDto.setWard(listing.getWard());
        listingDto.setAddress(listing.getAddress());

        listingDto.setCreatedAt(listing.getCreatedAt());
        listingDto.setUpdatedAt(listing.getUpdatedAt());
        listingDto.setExpiresAt(listing.getExpiresAt());
        listingDto.setPromotedUntil(listing.getPromotedUntil());
        listingDto.setStatus(listing.getStatus());
        listingDto.setVisibility(listing.getVisibility());

        //Chỉ dành cho pin
        listingDto.setVoltage(listing.getVoltage() != null ? listing.getVoltage() : null);
        listingDto.setBatteryChemistry(listing.getBatteryChemistry() != null ? listing.getBatteryChemistry() : null);
        listingDto.setMassKg(listing.getMassKg() != null ? listing.getMassKg() : null);
        listingDto.setDimensions(listing.getDimensions() != null ? listing.getDimensions() : null);

        listing.setHiddenAt(listing.getHiddenAt() != null ? listing.getHiddenAt() : null);
        listing.setDeletedAt(listing.getDeletedAt() != null ? listing.getDeletedAt() : null);

        listingDto.setRejectedReason(listing.getRejectedReason() != null ? listing.getRejectedReason() : null);
        listingDto.setRejectedAt(listing.getRejectedAt() != null ? listing.getRejectedAt() : null);


        listingDto.setPrevStatus(listing.getPrevStatus() != null ? listing.getPrevStatus() : null);
        listingDto.setPrevVisibility(listing.getPrevVisibility() != null ? listing.getPrevVisibility() : null);
        listingDto.setPrevExpiresAt(listing.getPrevExpiresAt() != null ? listing.getPrevExpiresAt() : null);
        if(listing.getBrandId() != null){
            listingDto.setBrandId(listing.getBrandId());
        }

        return listingDto;
    }

}
