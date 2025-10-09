package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Visibility;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ListingDto {
    private Long id;

    //Lấy id nếu có trong catlog
    private Long categoryId;
    private String categoryName;

    //Lấy nếu có trong catalog
    private Long brandId;

    //Lấy id và name nếu có trong catalog
    private Long modelId;

    //Lấy id của người bán
    private Long sellerId;

    //Lấy id vehicle nếu có trong catalog
    private Long productVehicleId;

    //Lấy id battery nếu có trong catalog
    private Long productBatteryId;

    //Lấy id của branch -> dùng cho kí gửi
    private Long branchId;

    //Lấy brand name của catalog hoặc của brand name ngoài
    private String brand;

    //Lấy mode name của catalog hoặc của model name ngoài
    private String model;

    private Integer year;

    private ListingStatus status;
    private Boolean isConsigned;
    private Boolean verified;

    private String title;
    private BigDecimal price;
    private BigDecimal aiSuggestedPrice;
    private Integer mileageKm;
    private BigDecimal sohPercent;
    private BigDecimal batteryCapacityKwh;
    private String color;
    private String description;

    private String province;
    private String district;
    private String ward;
    private String address;

    private Visibility visibility;
    private LocalDateTime promotedUntil;
    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
