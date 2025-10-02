package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.battery;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatteryListResponse {

    private Long id;

    private String brand;
    private String model;
    private String category;

    private String chemistry;
    private BigDecimal capacityKwh;
    private BigDecimal voltage;
    private BigDecimal weightKg;
    private String dimension;

    private String status;
    private LocalDateTime createdAt;

}
