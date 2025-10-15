package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingCardDTO {
    private Long id;
    private Long categoryId;
    private String title;
    private String brand;
    private String model;
    private Integer year;
    private String sellerName;
    private BigDecimal price;
    private String province;
    private BigDecimal batteryCapacityKwh;
    private BigDecimal sohPercent;
    private String mileageKm;
    private LocalDateTime createdAt;
    private String status;
    private String visibility;
    private Boolean isConsigned;

    // favorite info (may be null for search)
    private Long favoriteCount;
    private Boolean likedByCurrentUser;

    // full url composed from server.url + /api/files/images/{thumbnail}
    private String thumbnailUrl;
}
