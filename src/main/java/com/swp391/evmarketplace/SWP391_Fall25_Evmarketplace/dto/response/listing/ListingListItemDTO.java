package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingListItemDTO {
    private Long id;
    private Integer year;
    private String status;
    private String visibility;

    private String title;
    private BigDecimal batteryCapacityKwh;
    private LocalDateTime createdAt;
    private String province;
    private String mileageKm;
    private BigDecimal sohPercent;
    private String model;
    private String brand;
    private BigDecimal price;

    private Boolean isConsigned;
    private String sellerName;

    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime promotedUntil;
    private LocalDateTime hiddenAt;

    //Ngày xoá vĩnh viễn đối với tin đã yêu cầu xoá mềm
    private LocalDateTime purgeAt;
}
