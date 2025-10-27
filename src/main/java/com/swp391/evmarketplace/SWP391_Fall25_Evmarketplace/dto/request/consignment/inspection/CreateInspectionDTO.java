package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspection;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentInspectionResult;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateInspectionDTO {
    @NotNull
    private Long requestId;
    @NotBlank
    private String inspectionSummary;
    @NotNull @DecimalMin("0.0") @Digits(integer = 10, fraction = 2)
    private BigDecimal suggestedPrice;
    @NotNull
    private ConsignmentInspectionResult result;
}
