package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateListingResponse {
    private Long listingId;
    private String persistedStatus;
}
