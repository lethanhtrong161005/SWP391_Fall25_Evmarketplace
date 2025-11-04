package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignmentPayout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ConsignmentPayoutResponseDTO {
    Long id;
    Long saleId;
    Long agreementId;
    Long ownerId;
    BigDecimal paidAmount;
    String method;
    String note;
    Long recordedBy;
    LocalDateTime paidAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
