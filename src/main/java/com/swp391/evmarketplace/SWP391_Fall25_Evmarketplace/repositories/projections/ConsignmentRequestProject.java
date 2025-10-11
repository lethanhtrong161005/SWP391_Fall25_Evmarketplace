package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentRequestStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ConsignmentRequestProject {
    Long getId();
    String getAccountPhone();
    String getAccountName();
    String getItemType();
    String getCategory();
    String getIntendedFor();
    String getBrand();
    String getModel();
    Integer getYear();
    BigDecimal getBatteryCapacityKwh();
    BigDecimal getSohPercent();
    Integer getMileageKm();
    String getPreferredBranchName();
//    LocalDateTime getAppointmentTime();
    BigDecimal getOwnerExpectedPrice();
    ConsignmentRequestStatus getStatus();
    LocalDateTime getCreatedAt();

}
