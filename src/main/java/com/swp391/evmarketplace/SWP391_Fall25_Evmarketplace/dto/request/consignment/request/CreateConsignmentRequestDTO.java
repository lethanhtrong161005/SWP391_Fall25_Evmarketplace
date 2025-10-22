package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.CategoryCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateConsignmentRequestDTO {
    @NotNull(message = "itemType is required")
    private ItemType itemType;  // VEHICLE/BATTERY

    @NotNull(message = "categoryId is required")
    private Long categoryId;    // FK -> category

    @NotBlank
    @Size(max = 100)
    private String brand;
    private Long brandId;

    @NotBlank
    @Size(max = 100)
    private String model;
    private Long modelId;

    @NotNull
    @Min(value = 1900)
    @Max(value = 2100)
    private Integer year;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 6, fraction = 2)
    private BigDecimal batteryCapacityKwh;

    @NotNull
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "100.00")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal sohPercent;

    @PositiveOrZero
    @NotNull
    private Integer mileageKm;

    @NotNull
    private Long preferredBranchId;   // FK -> branch

    @NotNull(message = "")
    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 12, fraction = 2)
    private BigDecimal ownerExpectedPrice;

    @Size(max = 10000)
    private String note;
}



