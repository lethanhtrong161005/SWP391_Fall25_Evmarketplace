package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ConsignmentInspectionProjection {
    Long getId();
    Long getRequestId();
    String getRequestOwnerPhone();
    String getRequestOwnerFullName();
    Long getBranchId();
    String getResult();
    String getInspectionSummary();
    BigDecimal getSuggestedPrice();
    boolean getIsActive();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
