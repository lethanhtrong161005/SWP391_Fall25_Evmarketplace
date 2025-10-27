package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CancelConsignmentRequestDTO {
    @NotNull
    private Long requestId;

    @NotBlank
    private String cancelledReason;
}
