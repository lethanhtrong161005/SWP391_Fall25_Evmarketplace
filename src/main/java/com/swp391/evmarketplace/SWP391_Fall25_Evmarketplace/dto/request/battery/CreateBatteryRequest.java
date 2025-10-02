package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.battery;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBatteryRequest {
    @NotNull(message = "Model is required")
    private Long modelId;

    private String chemistry; // optional

    @NotNull(message = "Capacity (kWh) is required")
    @DecimalMin(value = "0.01", message = "Capacity must be > 0")
    private BigDecimal capacityKwh;

    @NotNull(message = "Voltage (V) is required")
    @DecimalMin(value = "0.01", message = "Voltage must be > 0")
    private BigDecimal voltage;

    @DecimalMin(value = "0.00", message = "Weight must be >= 0")
    private BigDecimal weightKg; // optional

    @Size(max = 100, message = "Dimension max length = 100")
    private String dimension; // optional
}
