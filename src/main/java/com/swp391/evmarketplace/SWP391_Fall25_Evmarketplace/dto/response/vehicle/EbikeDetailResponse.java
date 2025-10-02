package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.vehicle;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EbikeDetailResponse {
    private String frameSize;
    private String wheelSize;
    private BigDecimal weightKg;
    private Integer maxLoad;
    private Short gears;
    private Boolean removableBattery;
    private Boolean throttle;
}
