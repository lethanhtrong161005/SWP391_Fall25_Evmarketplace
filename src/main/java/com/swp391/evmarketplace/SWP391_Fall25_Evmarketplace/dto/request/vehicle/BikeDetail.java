package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.vehicle;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.BrakeType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.MotorLocation;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BikeDetail {
    @NotNull
    @Enumerated(EnumType.STRING)
    private MotorLocation motorLocation; // HUB|MID

    @NotBlank
    @Size(max = 20)
    private String wheelSize;            // "12\"", "14\"", "17\""...

    @NotNull
    @Enumerated(EnumType.STRING)
    private BrakeType brakeType;         // DISC|DRUM

    @NotNull @DecimalMin("0.01") @Digits(integer = 6, fraction = 2)
    private BigDecimal weightKg;
}
