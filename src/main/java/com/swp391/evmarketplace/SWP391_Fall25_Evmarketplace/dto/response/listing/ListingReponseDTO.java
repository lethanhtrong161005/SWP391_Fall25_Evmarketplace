package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ListingReponseDTO {

    private Long id;
    private String title;

    private Long productVehicleId;
    private Long productBatteryId;

    private Long sellerId;

    private String brand;
    private String model;
    private Integer year;

    private BigDecimal batteryCapacityKwh;
    private BigDecimal sohPercent;
    private Integer  mileageKm;
    private String color;
    private String description;

    private BigDecimal price;
    private boolean verified;
    private String visibility;
    private String status;

    private String province;
    private String city;
    private String address;

    private LocalDateTime promotedUntil;

    private Long branchId;

    private String thumbnail;

    private boolean isConsigned;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
