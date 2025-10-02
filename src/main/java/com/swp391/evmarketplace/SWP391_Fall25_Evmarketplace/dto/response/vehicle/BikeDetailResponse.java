package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.vehicle;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.BrakeType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.MotorLocation;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BikeDetailResponse {
    private MotorLocation motorLocation;
    private String wheelSize;
    private BrakeType brakeType;
    private BigDecimal weightKg;
}
