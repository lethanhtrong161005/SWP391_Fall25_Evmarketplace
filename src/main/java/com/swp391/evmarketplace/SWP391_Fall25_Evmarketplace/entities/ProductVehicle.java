package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AcConnectorType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.DcConnectorType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ModelStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "product_vehicle",
        indexes = {
                @Index(name = "idx_pv_category", columnList = "category_id"),
                @Index(name = "idx_pv_brand",    columnList = "brand_id"),
                @Index(name = "idx_pv_model",    columnList = "model_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_pv_name", columnNames = {"brand_id","model_id","name","release_year"})
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProductVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Metadata ---
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pv_category"))
    private Category category;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pv_brand"))
    private Brand brand;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pv_model"))
    private Model model;

    @Column(nullable = false, length = 255)
    private String name;

    @Lob
    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "release_year", columnDefinition = "smallint unsigned")
    private Integer releaseYear;

    // --- Thông số CHUNG (Must-have) đã gộp vào product_vehicle ---
    @Column(name = "battery_capacity_kwh", nullable = false, precision = 6, scale = 2)
    private BigDecimal batteryCapacityKwh;                      // usable kWh

    @Column(name = "range_km", nullable = false)
    private Integer rangeKm;                                    // km

    @Column(name = "motor_power_kw", nullable = false, precision = 5, scale = 2)
    private BigDecimal motorPowerKw;                            // kW

    @Column(name = "ac_charging_kw", nullable = false, precision = 5, scale = 2)
    private BigDecimal acChargingKw;                            // kW (OBC)

    @Column(name = "dc_charging_kw", precision = 5, scale = 2)
    private BigDecimal dcChargingKw;                            // kW (nullable)

    @Enumerated(EnumType.STRING)
    @Column(name = "ac_connector", nullable = false, length = 16)
    private AcConnectorType acConnector;                        // TYPE1/TYPE2/NACS/GBT/OTHER

    @Enumerated(EnumType.STRING)
    @Column(name = "dc_connector", nullable = false, length = 16)
    private DcConnectorType dcConnector;                        // CCS1/CCS2/CHADEMO/NACS/GBT/NONE/OTHER

    // --- System columns ---
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // --- Detail quan hệ 1–1 (ô tô / xe máy / xe đạp) ---
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ProductCarDetail carDetail;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ProductBikeDetail bikeDetail;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ProductEbikeDetail ebikeDetail;

    @Column(name = "status" , nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.ACTIVE;

    // convenience setters để set 2 chiều
    public void setCarDetail(ProductCarDetail detail) {
        if (detail != null) detail.setProduct(this);
        this.carDetail = detail;
    }

    public void setBikeDetail(ProductBikeDetail detail) {
        if (detail != null) detail.setProduct(this);
        this.bikeDetail = detail;
    }

    public void setEbikeDetail(ProductEbikeDetail detail) {
        if (detail != null) detail.setProduct(this);
        this.ebikeDetail = detail;
    }

}
