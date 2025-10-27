package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.InspectionScheduleStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface StaffScheduleRow {
    Long getId();
    LocalDate getScheduleDate();
    String getScheduledByName();
    String getShiftCode();
    LocalTime getShiftStartTime();
    LocalTime getShiftEndTime();
    InspectionScheduleStatus getStatus();
    LocalDateTime getCheckedInAt();
}
