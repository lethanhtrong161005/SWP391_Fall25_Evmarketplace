package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.battery;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBatteryRequest {
    @Positive(message = "modelId must be > 0")
    private Long modelId;      // optional, change target model

    // Thông tin kỹ thuật (chỉ cập nhật khi != null hoặc String có text)
    @Size(max = 50, message = "Chemistry max length = 50")
    private String chemistry;    // optional

    @DecimalMin(value = "0.01", message = "Capacity must be > 0")
    private BigDecimal capacityKwh;   // optional

    @DecimalMin(value = "0.01", message = "Voltage must be > 0")
    private BigDecimal voltage;       // optional

    @DecimalMin(value = "0.00", message = "Weight must be >= 0")
    private BigDecimal weightKg;      // optional

    @Size(max = 100, message = "Dimension max length = 100")
    private String dimension;         // optional

    // ACTIVE | HIDDEN (service sẽ parse/validate)
    private String status;            // optional
}
