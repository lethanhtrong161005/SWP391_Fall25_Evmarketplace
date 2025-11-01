package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Visibility;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class ConsignmentListingFilter {
    private String q;

    private ItemType itemType;            // VEHICLE | BATTERY
    private ListingStatus status;
    private Visibility visibility;
    private Long categoryId;
    private Long brandId;
    private Long modelId;

    // ---- Phổ biến cho cả xe & pin ----
    @NumberFormat private BigDecimal priceMin;
    @NumberFormat private BigDecimal priceMax;

    private Integer yearMin;
    private Integer yearMax;

    private Integer mileageMax;           // km (xe)
    private BigDecimal sohMin;            // % (xe/pin)

    // ---- Dành riêng cho PIN (map với listing.voltage_v, battery_chemistry, mass_kg, dimensions_mm) ----
    private BigDecimal batteryCapacityMinKwh;    // đã có
    private BigDecimal batteryCapacityMaxKwh;    // thêm
    private BigDecimal voltageMinV;              // listing.voltage_v
    private BigDecimal voltageMaxV;
    private BigDecimal massMaxKg;                // listing.mass_kg (lọc <=)
    private Set<String> chemistries;             // ví dụ: ["LFP","NMC"] khớp listing.battery_chemistry (case-insensitive)

    // Sắp xếp
    private String sort;                 // createdAt|updatedAt|price|year|batteryCapacityKwh|voltageV
    private String dir;                  // asc|desc

    private String province;
}
