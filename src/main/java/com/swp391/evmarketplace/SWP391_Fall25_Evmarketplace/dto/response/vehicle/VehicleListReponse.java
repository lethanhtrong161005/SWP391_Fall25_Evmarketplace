package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.vehicle;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AcConnectorType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.DcConnectorType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ProductStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleListReponse {
    private Long id;

    // Meta
    private String category;     // EV_CAR / E_MOTORBIKE / E_BIKE (từ category.name hoặc code)
    private String brand;        // brand.name
    private String model;        // model.name
    private String name;         // vehicle display name
    private Integer releaseYear;

    private String status;

    // Spec chung (must-have)
    private BigDecimal batteryCapacityKwh;
    private Integer     rangeKm;
    private BigDecimal  motorPowerKw;
    private BigDecimal  acChargingKw;
    private BigDecimal  dcChargingKw; // có thể null
    private AcConnectorType acConnector;
    private DcConnectorType dcConnector;

    private CarDetailResponse   car;
    private BikeDetailResponse bike;
    private EbikeDetailResponse ebike;
}
