package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ConsignmentAgreementProjection {
    Long getId();
    Long getRequestId();
    Long getOwnerId();
    Long getBranchId();
    BigDecimal getCommissionPercent();
    BigDecimal getAcceptablePrice();
    BigDecimal getDepositAmount();
    String getStatus();
    String getDuration();
    LocalDateTime getStartAt();
    LocalDateTime getExpireAt();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
