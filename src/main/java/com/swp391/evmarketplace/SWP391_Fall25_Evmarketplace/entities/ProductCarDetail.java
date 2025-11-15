package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.vehicle.CarDetailResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.BodyType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Drivetrain;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_car_detail")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProductCarDetail {
    @Id
    @Column(name = "product_id")
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "product_id",
            foreignKey = @ForeignKey(name = "fk_car_product"))
    private ProductVehicle product;

    @Column(name = "seating_capacity", nullable = false)
    private Integer seatingCapacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "body_type", nullable = false, length = 16)
    private BodyType bodyType; // SEDAN|SUV|HATCHBACK|MPV|PICKUP|OTHER

    @Enumerated(EnumType.STRING)
    @Column(name = "drivetrain", nullable = false, length = 8)
    private Drivetrain drivetrain; // FWD|RWD|AWD

    @Column(name = "trunk_rear_l", nullable = false)
    private Integer trunkRearL; // dung tích cốp sau (L)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public CarDetailResponse toDto(ProductCarDetail c){
        CarDetailResponse dto = new CarDetailResponse();
        dto.setSeatingCapacity(c.getSeatingCapacity());
        dto.setBodyType(c.getBodyType());
        dto.setDrivetrain(c.getDrivetrain());
        dto.setTrunkRearL(c.getTrunkRearL());
        return dto;
    }

}
