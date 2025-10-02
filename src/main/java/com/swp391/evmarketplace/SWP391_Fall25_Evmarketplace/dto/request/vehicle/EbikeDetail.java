package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.vehicle;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EbikeDetail {
    @NotBlank @Size(max = 12)
    private String frameSize;            // S/M/L/XL hoặc "52cm"

    @NotBlank @Size(max = 20)
    private String wheelSize;            // 26"/27.5"/29"...

    @NotNull @DecimalMin("0.01")
    @Digits(integer = 6, fraction = 2)
    private BigDecimal weightKg;

    @NotNull @Min(1)
    private Integer maxLoad;             // kg

    @NotNull @Min(1)
    private Integer gears;               // số líp/tốc độ

    @NotNull
    private Boolean removableBattery = Boolean.TRUE;

    @NotNull
    private Boolean throttle = Boolean.FALSE;
}
