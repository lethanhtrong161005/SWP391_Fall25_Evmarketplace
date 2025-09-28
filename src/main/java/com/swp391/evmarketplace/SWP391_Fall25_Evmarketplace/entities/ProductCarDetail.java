package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_car_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCarDetail {
    @Id
    @Column(name = "product_id")
    private Long id;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(
            name = "product_id",
            foreignKey = @ForeignKey(name = "fk_car_product")
    )
    private ProductVehicle product;

    @Column(name = "seating_capacity", nullable = false)
    private Integer seatingCapacity;

    @Column(name = "trunk_capacity")
    private Integer trunkCapacity;

    @Column(name = "airbags")
    private Integer airbags;

    @Column(name = "has_abs", nullable = false)
    private Boolean hasAbs = Boolean.FALSE;

    @Column(name = "has_autopilot", nullable = false)
    private Boolean hasAutopilot = Boolean.FALSE;
}
