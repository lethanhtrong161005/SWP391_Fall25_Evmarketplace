package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchListingRequestDTO {
    private String key;           // brand, model, title

    private Integer yearFrom;       // range
    private Integer yearTo;

    private BigDecimal capacityMin; // battery_capacity_kwh
    private BigDecimal capacityMax;

    private BigDecimal priceMin;    // price
    private BigDecimal priceMax;

    private Integer mileageMin;     // mileage_km
    private Integer mileageMax;

    private BigDecimal sohMin;      // soh_percent
    private BigDecimal sohMax;
}
