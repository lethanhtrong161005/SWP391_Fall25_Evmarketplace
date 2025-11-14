package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.vehicle.BikeDetailResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.BrakeType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.MotorLocation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_bike_detail")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProductBikeDetail {

    @Id
    @Column(name = "product_id")
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "product_id",
            foreignKey = @ForeignKey(name = "fk_bike_product"))
    private ProductVehicle product;

    @Enumerated(EnumType.STRING)
    @Column(name = "motor_location", nullable = false, length = 8)
    private MotorLocation motorLocation; // HUB|MID

    @Column(name = "wheel_size", nullable = false, length = 20)
    private String wheelSize; // "12\"", "14\"", "17\""...

    @Enumerated(EnumType.STRING)
    @Column(name = "brake_type", nullable = false, length = 16)
    private BrakeType brakeType; // DISC|DRUM

    @Column(name = "weight_kg", nullable = false, precision = 6, scale = 2)
    private BigDecimal weightKg;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public BikeDetailResponse toDto(ProductBikeDetail b){
        BikeDetailResponse dto = new BikeDetailResponse();
        dto.setMotorLocation(b.getMotorLocation());
        dto.setWheelSize(b.getWheelSize());
        dto.setBrakeType(b.getBrakeType());
        dto.setWeightKg(b.getWeightKg());
        return dto;
    }

}
