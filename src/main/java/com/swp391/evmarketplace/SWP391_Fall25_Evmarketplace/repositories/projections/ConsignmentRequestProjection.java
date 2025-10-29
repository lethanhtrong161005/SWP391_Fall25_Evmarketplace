package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentRequestStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ConsignmentRequestProjection {
    Long getId();
    String getAccountPhone();
    String getAccountName();
    Long getStaffId();
    String getRejectedReason();
    String getItemType();

    Long getCategoryId();
    String getCategory();

    Long getBrandId();
    String getBrand();

    Long getModelId();
    String getModel();
    Integer getYear();
    BigDecimal getBatteryCapacityKwh();
    BigDecimal getSohPercent();
    Integer getMileageKm();

    Long getPreferredBranchId();
    String getPreferredBranchName();
    BigDecimal getOwnerExpectedPrice();
    ConsignmentRequestStatus getStatus();
    LocalDateTime getCreatedAt();
    Long getCancelledById();
    LocalDateTime getCancelledAt();
    String getCancelledReason();
}
