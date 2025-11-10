package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.settlement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsignmentSettlementResponseDto {
    private Long id;
    private Long orderId;
    private Long agreementId;
    private Long ownerId;
    private BigDecimal grossAmount;
    private BigDecimal commissionPercent;
    private BigDecimal commissionAmount;
    private BigDecimal ownerReceiveAmount;
    private String method;
    private String status;
    private LocalDateTime paidAt;
    private String note;
    private String mediaUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
