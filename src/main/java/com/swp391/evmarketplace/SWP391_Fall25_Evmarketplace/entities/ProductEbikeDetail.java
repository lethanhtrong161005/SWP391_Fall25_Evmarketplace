package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_ebike_detail" )
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductEbikeDetail {

    @Id
    @Column(name = "product_id")
    private Long id;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "product_id",
            foreignKey = @ForeignKey(name = "fk_ebike_product"))
    private ProductVehicle product;

    @Column(name = "frame_material", length = 50)
    private String frameMaterial;

    @Column(name = "max_load")
    private Integer maxLoad;

    @Column(name = "gears")
    private Integer gears;

    @Column(name = "pedal_assist", nullable = false)
    private Boolean pedalAssist = Boolean.TRUE;
}
