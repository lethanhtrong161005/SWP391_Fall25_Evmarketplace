package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ListingListProjection {
    Long getId();
    String getTitle();
    String getBrand();
    String getModel();
    Integer getYear();
    String getSellerName();
    BigDecimal getPrice();
    String getProvince();
    BigDecimal getBatteryCapacityKwh();
    BigDecimal getSohPercent();
    String getMileageKm();
    LocalDateTime getCreatedAt();
    String getListingStatus();
    String getVisibility();
    boolean getIsConsigned();
}