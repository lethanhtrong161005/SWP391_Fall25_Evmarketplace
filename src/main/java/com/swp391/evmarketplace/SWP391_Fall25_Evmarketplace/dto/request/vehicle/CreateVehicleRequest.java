package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.vehicle;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AcConnectorType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.DcConnectorType;
import jakarta.validation.Valid;
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
public class CreateVehicleRequest {

    // --- Metadata ---
    @NotNull(message = "categoryId is required")
    private Long categoryId; // EV_CAR / E_MOTORBIKE / E_BIKE

    @NotNull(message = "brandId is required")
    private Long brandId;

    @NotNull(message = "modelId is required")
    private Long modelId;

    @NotBlank @Size(max = 255)
    private String name;

    @Size(max = 4000)
    private String description;

    @Min(1900) @Max(2100)
    private Integer releaseYear;

    // --- Thông số CHUNG (must-have) ---
    @NotNull @DecimalMin(value = "0.01")
    @Digits(integer = 4, fraction = 2) // tối đa 9999.99 kWh
    private BigDecimal batteryCapacityKwh;

    @NotNull @Min(1)
    private Integer rangeKm;

    @NotNull @DecimalMin(value = "0.01")
    @Digits(integer = 3, fraction = 2) // tối đa 999.99 kW
    private BigDecimal motorPowerKw;

    @NotNull @DecimalMin(value = "0.01")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal acChargingKw;

    @DecimalMin(value = "0.01")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal dcChargingKw; // nullable nếu không hỗ trợ DC

    @NotNull
    private AcConnectorType acConnector; // TYPE1|TYPE2|NACS|GBT|OTHER

    @NotNull
    private DcConnectorType dcConnector; // CCS1|CCS2|CHADEMO|NACS|GBT|NONE|OTHER

    @Valid
    private CarDetail carDetail;

    @Valid
    private BikeDetail bikeDetail;

    @Valid
    private EbikeDetail ebikeDetail;

}
