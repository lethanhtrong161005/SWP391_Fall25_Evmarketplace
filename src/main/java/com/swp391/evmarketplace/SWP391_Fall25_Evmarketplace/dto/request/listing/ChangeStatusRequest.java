package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeStatusRequest {
    @NotNull
    private Long id;
    private ListingStatus status;
}
