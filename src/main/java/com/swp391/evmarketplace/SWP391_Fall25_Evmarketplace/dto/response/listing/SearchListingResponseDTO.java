package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchListingResponseDTO {
    private Long id;
    private String title, brand, model, province, city;
    private Integer year, mileageKm;
    private BigDecimal batteryCapacityKwh, sohPercent, price;
    private LocalDateTime createdAt;

}
