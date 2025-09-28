package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "product_vehicle",
        indexes = {
                @Index(name = "idx_pv_category", columnList = "category_id"),
                @Index(name = "idx_pv_brand", columnList = "brand_id"),
                @Index(name = "idx_pv_model", columnList = "model_id")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_pv_category")
    )
    private Category category;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "brand_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pv_brand")
    )
    private Brand brand;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "model_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pv_model")
    )
    private Model model;

    @Column(nullable = false, length = 255)
    private String name;

    @Lob
    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "release_year", columnDefinition = "smallint unsigned")
    private Integer releaseYear;

    @Column(name = "motor_power")
    private Integer motorPower; //W

    @Column(name = "battery_capacity", precision = 6, scale = 2)
    private BigDecimal batteryCapacity; // kWh

    @Column(name = "range_km")
    private Integer rangeKm;

    @Column(name = "seating_capacity")
    private Integer seatingCapacity;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(
            mappedBy = "product",
            cascade = CascadeType.ALL, orphanRemoval = true
    )
    private ProductCarDetail carDetail;

    @OneToOne(
            mappedBy = "product",
            cascade = CascadeType.ALL, orphanRemoval = true
    )
    private ProductBikeDetail bikeDetail;


    @OneToOne(
            mappedBy = "product",
            cascade = CascadeType.ALL, orphanRemoval = true
    )
    private ProductEbikeDetail ebikeDetail;

    public void setCarDetail(ProductCarDetail detail) {
        if(detail != null) detail.setProduct(this);
        this.carDetail = detail;
    }

    public void setBikeDetail(ProductBikeDetail detail) {
        if(detail != null) detail.setProduct(this);
        this.bikeDetail = detail;
    }

    public void setEbikeDetail(ProductEbikeDetail detail) {
        if(detail != null) detail.setProduct(this);
        this.ebikeDetail = detail;
    }

}
