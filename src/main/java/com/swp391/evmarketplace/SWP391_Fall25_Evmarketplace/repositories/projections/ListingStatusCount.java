package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;


public interface ListingStatusCount {
    ListingStatus getStatus();
    long getTotal();
}
