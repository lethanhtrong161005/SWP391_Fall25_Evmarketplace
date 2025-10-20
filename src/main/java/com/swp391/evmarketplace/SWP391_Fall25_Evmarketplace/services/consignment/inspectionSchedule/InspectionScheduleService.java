package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.inspectionSchedule;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspectionSchedule.CancelScheduleDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspectionSchedule.CreateInspectionScheduleDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftAvailabilityDayDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;

import java.time.LocalDate;

public interface InspectionScheduleService {
    BaseResponse<Void> createSchedule(CreateInspectionScheduleDTO dto);
    BaseResponse<ShiftAvailabilityDayDTO> getAvailability(Long staffId, Long branchId, ItemType itemType, LocalDate date);
    BaseResponse<Void> cancelSchedule(Long scheduleId,CancelScheduleDTO dto);
    BaseResponse<Void> checkin(Long scheduleId);
}
