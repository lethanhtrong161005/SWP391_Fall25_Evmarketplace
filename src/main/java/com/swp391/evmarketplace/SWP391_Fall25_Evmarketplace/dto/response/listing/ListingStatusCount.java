package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;


public interface ListingStatusCount {
    ListingStatus getStatus();
    long getTotal();
}
