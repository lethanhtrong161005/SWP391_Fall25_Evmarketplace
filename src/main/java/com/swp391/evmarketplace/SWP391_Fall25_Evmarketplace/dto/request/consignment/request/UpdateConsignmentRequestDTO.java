package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateConsignmentRequestDTO {
    private ItemType itemType;

    private Long categoryId;
    private Long brandId;
    private Long modelId;
    private String brand;
    private String model;

    @Min(1900)
    @Max(2100)
    private Integer year;

    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 6, fraction = 2)
    private BigDecimal batteryCapacityKwh; // optional

    @DecimalMin(value = "0.00") @DecimalMax(value = "100.00")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal sohPercent;  // optional

    @PositiveOrZero
    private Integer mileageKm;      // optional

    private Long preferredBranchId; // optional

    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 12, fraction = 2)
    private BigDecimal ownerExpectedPrice; // optional

    @Size(max = 10000)
    private String note;            // optional

    // Media control
    private List<Long> deleteMediaIds;  // optional
}
