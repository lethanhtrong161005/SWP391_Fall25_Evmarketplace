package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.InspectionScheduleStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface InspectionScheduleDetailProjection {
    Long getId();
    Long getRequestId();

    // Staff
    Long getStaffId();
    String getStaffName();

    // Branch
    Long getBranchId();
    String getBranchName();

    // Shift
    Long getShiftId();
    String getShiftCode();
    java.time.LocalTime getShiftStartTime();
    java.time.LocalTime getShiftEndTime();

    // Audit
    String getScheduledByName();
    String getCancelledByName();

    LocalDate getScheduleDate();
    InspectionScheduleStatus getStatus();
    LocalDateTime getCheckedInAt(); // lưu ý field bạn đặt là checkedInAt
    LocalDateTime getCancelledAt();
    String getCancelledReason();
    String getNote();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
