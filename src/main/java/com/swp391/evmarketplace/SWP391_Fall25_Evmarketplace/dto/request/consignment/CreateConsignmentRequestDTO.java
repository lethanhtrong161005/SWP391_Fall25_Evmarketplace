package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.CategoryCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.validation.ValidPhone;
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
    // phone -> account
    @ValidPhone
    private String phoneNumber;

    private ItemType itemType;  // VEHICLE/BATTERY

    @NotNull(message = "categoryId is required")
    private Long categoryId;    // FK -> category

    // only for BATTERY
    @NotNull
    private CategoryCode intendedFor; // EV_CAR/E_MOTORBIKE/E_BIKE

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
    private Integer year; // optional

    // > 0 và đúng định dạng 6,2
    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 6, fraction = 2)
    private BigDecimal batteryCapacityKwh;

    // 0..100, định dạng 5,2
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

    @NotNull
    @Future
    private LocalDateTime appointmentTime;

    @NotNull(message = "")
    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 12, fraction = 2)
    private BigDecimal ownerExpectedPrice;

    @Size(max = 10000)
    private String note;

    @NotNull()
    private ConsignmentStatus status;
}

