package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.battery.BatteryListResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_battery",
        indexes = {
                @Index(name = "idx_pb_brand", columnList = "brand_id"),
                @Index(name = "idx_pb_model", columnList = "model_id"),
                @Index(name = "idx_pb_category", columnList = "category_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBattery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "brand_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pb_brand")
    )
    private Brand brand;

    @ManyToOne(optional = false)
    @JoinColumn(name = "model_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pb_model"))
    private Model model;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pb_category"))
    private Category category;

    @Column(name = "chemistry", length = 50)
    private String chemistry;

    @Column(name = "capacity_kwh", precision = 6, scale = 2, nullable = false)
    private BigDecimal capacityKwh;

    @Column(name = "voltage", precision = 6, scale = 2, nullable = false)
    private BigDecimal voltage;

    @Column(name = "weight_kg", precision = 6, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "dimension", length = 100)
    private String dimension;


    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    public BatteryListResponse toDto(ProductBattery productBattery) {
        BatteryListResponse dto = new BatteryListResponse();
        dto.setId(productBattery.getId());

        dto.setStatus(productBattery.getStatus().name());

        dto.setCategory(productBattery.getCategory().getName());
        dto.setBrand(productBattery.getBrand().getName());
        dto.setModel(productBattery.getModel().getName());

        dto.setChemistry(productBattery.getChemistry());
        dto.setCapacityKwh(productBattery.getCapacityKwh());
        dto.setVoltage(productBattery.getVoltage());
        dto.setWeightKg(productBattery.getWeightKg());
        dto.setDimension(productBattery.getDimension());
        dto.setCreatedAt(productBattery.getCreatedAt());

        return dto;
    }
}
