package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignmentPayout;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ConsignmentPayoutCreateRequestDTO {
    @NotNull
    Long settlementId;

    Long agreementId;

    @NotNull
    Long ownerId;

    @NotNull
    @Positive
    BigDecimal paidAmount;

    @NotNull
    String method;

    String note;

    @NotNull
    LocalDateTime paidAt;
}
