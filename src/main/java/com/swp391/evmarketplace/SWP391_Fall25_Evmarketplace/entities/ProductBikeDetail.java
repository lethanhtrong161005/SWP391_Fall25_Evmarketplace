package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "product_bike_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductBikeDetail {

    @Id
    @Column(name = "product_id")
    private Long id;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "product_id",
            foreignKey = @ForeignKey(name = "fk_bike_product"))
    private ProductVehicle product;

    @Column(name = "brake_type", length = 50)
    private String brakeType;

    @Column(name = "wheel_size", length = 20)
    private String wheelSize;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "fast_charging", nullable = false)
    private Boolean fastCharging = Boolean.FALSE;
}
