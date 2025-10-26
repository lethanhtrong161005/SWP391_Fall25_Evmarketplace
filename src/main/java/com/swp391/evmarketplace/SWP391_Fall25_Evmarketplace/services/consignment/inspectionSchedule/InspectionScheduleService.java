package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.inspectionSchedule;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspectionSchedule.CancelScheduleDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspectionSchedule.CreateInspectionScheduleDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftAvailabilityDayDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentRequestStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.InspectionScheduleStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.InspectionScheduleDetailProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.StaffScheduleRow;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface InspectionScheduleService {
    BaseResponse<Void> createSchedule(CreateInspectionScheduleDTO dto);
    BaseResponse<ShiftAvailabilityDayDTO> getAvailability(Long requestId, LocalDate date);
    BaseResponse<Void> cancelSchedule(Long scheduleId,CancelScheduleDTO dto);
    BaseResponse<Void> checkin(Long scheduleId);

    BaseResponse<List<InspectionScheduleDetailProjection>> getScheduleByRequestId(Long id, Collection<InspectionScheduleStatus> statuses);
    BaseResponse<List<StaffScheduleRow>> getListScheduleByDate(LocalDate date, Collection<InspectionScheduleStatus> statuses);
}
