package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectListingRequest {
    @NotNull(message = "Reason is required")
    @NotEmpty
    private String reason;
}
