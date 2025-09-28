package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "battery_compatible_model",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_battery_vehicle",
                columnNames = {"battery_id", "vehicle_model_id"}
        ),
        indexes = {
                @Index(name = "idx_bcm_battery", columnList = "battery_id"),
                @Index(name = "idx_bcm_vehicle", columnList = "vehicle_model_id")
})
public class BatteryCompatibleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne( optional = false)
    @JoinColumn(name = "battery_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_bcm_battery"))
    private ProductBattery battery;


    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_model_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_bcm_vehicle"))
    private ProductVehicle vehicle;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
