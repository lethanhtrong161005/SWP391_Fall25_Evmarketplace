package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_ebike_detail")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProductEbikeDetail {

    @Id
    @Column(name = "product_id")
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "product_id",
            foreignKey = @ForeignKey(name = "fk_ebike_product"))
    private ProductVehicle product;

    @Column(name = "frame_size", nullable = false, length = 12)
    private String frameSize; // S/M/L/XL hoặc "52cm"

    @Column(name = "wheel_size", nullable = false, length = 20)
    private String wheelSize; // 26"/27.5"/29"...

    @Column(name = "weight_kg", nullable = false, precision = 6, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "max_load", nullable = false)
    private Integer maxLoad; // kg

    @Column(name = "gears", nullable = false)
    private Short gears; // TINYINT -> Short/Byte; dùng Short cho an toàn

    @Column(name = "removable_battery", nullable = false)
    private Boolean removableBattery = Boolean.TRUE;

    @Column(name = "throttle", nullable = false)
    private Boolean throttle = Boolean.FALSE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
