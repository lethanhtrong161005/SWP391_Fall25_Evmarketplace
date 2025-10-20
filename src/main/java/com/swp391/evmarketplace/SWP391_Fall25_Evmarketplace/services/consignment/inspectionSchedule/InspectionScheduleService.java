package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.inspectionSchedule;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspectionSchedule.CreateInspectionScheduleDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftAvailabilityDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;

import java.time.LocalDate;
import java.util.List;

public interface InspectionScheduleService {
    BaseResponse<Void> createSchedule(CreateInspectionScheduleDTO dto);
    BaseResponse<List<ShiftAvailabilityDTO>> getAvailability(Long staffId, Long branchId, ItemType itemType, LocalDate date);


}
