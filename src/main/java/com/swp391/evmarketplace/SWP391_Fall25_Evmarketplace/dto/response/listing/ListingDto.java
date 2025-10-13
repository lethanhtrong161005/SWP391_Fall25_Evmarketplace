package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Visibility;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

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


    //Chỉ dành cho pin
    private BigDecimal voltage;
    private String batteryChemistry;
    private BigDecimal massKg;
    private String dimensions;

    private String province;
    private String district;
    private String ward;
    private String address;

    private Visibility visibility;
    private LocalDateTime promotedUntil;
    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDateTime hiddenAt;
    private LocalDateTime deletedAt;

    private ListingStatus prevStatus;
    private Visibility prevVisibility;
    private LocalDateTime prevExpiresAt;

    private String rejectedReason;
    private LocalDateTime rejectedAt;

}
