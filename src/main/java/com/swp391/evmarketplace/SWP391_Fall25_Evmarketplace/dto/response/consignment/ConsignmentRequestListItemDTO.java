package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsignmentRequestListItemDTO {
    private Long id;
    private String accountPhone;
    private String accountName;
    private String itemType; // keep as String to align with current projection
    private String category;
    private String brand;
    private String model;
    private Integer year;
    private BigDecimal batteryCapacityKwh;
    private BigDecimal sohPercent;
    private Integer mileageKm;
    private String preferredBranchName;
    private BigDecimal ownerExpectedPrice;
    private ConsignmentRequestStatus status;
    private LocalDateTime createdAt;
    private List<String> mediaUrls;
}
