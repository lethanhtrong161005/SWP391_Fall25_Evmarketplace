package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateConsignmentRequestDTO {
    @NotNull(message = "request id is required")
    private Long id;
    private Long categoryId;
    private Long brandId;
    private Long modelId;
    private String brand;
    private String model;
    private Integer year;

    private BigDecimal ownerExpectedPrice;
    private BigDecimal batteryCapacityKwh;
    private BigDecimal sohPercent;
    private Long mileageKm;

    private Long preferredBranchId;
    private String note;

}
