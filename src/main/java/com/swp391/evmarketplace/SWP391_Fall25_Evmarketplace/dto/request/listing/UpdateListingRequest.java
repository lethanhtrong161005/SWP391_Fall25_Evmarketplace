package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Visibility;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateListingRequest {
    private Long categoryId;
    private String categoryCode;
    private ItemType itemType;

    private String brand;
    private Long brandId;
    private String model;
    private Long modelId;

    private String title;
    private Integer year;
    private String color;

    private BigDecimal batteryCapacityKwh;
    private BigDecimal sohPercent;
    private Integer mileageKm;

    private BigDecimal price;
    private String description;

    private String province;
    private String district;
    private String ward;
    private String address;

    private ListingStatus status;   // PENDING/...
    private String postType;        // FREE|PAID
    private Visibility visibility;  // NORMAL|BOOSTED

    // Pin
    private BigDecimal voltageV;
    private String batteryChemistry;
    private BigDecimal massKg;
    private String dimensionsMm;
}
