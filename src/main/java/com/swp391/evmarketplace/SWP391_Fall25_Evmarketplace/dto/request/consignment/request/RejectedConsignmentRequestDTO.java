package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectedConsignmentRequestDTO extends AcceptedConsignmentRequestDTO {
    @NotBlank(message = "rejected reason is required")
    String rejectedReason;
}
