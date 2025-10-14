package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.shift;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftAvailabilityDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.InspectionScheduleStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.InspectionScheduleRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ShiftTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShiftAvailabilityService {

    @Autowired
    private ShiftTemplateRepository shiftTemplateRepository;

    @Autowired
    private InspectionScheduleRepository inspectionScheduleRepository;

    //for user
    // count as occupying a slot
    private static final List<InspectionScheduleStatus> OCCUPIED = List.of(
            InspectionScheduleStatus.SCHEDULED,
            InspectionScheduleStatus.CHECKED_IN,
            InspectionScheduleStatus.IN_PROGRESS
    );

    //lấy danh sách ca làm trống
    public BaseResponse<List<ShiftAvailabilityDTO>> getAvailability(Long branchId, ItemType itemType, LocalDate date) {
        var res = new BaseResponse<List<ShiftAvailabilityDTO>>();
        if (branchId == null || itemType == null || date == null) {
            res.setStatus(400);
            res.setSuccess(false);
            res.setMessage("BAD_REQUEST");
            return res;
        }

        //list ca làm theo loại(xe/ pin) đang hoạt động tại 1 cơ sở
        var shifts = shiftTemplateRepository.findByBranch_IdAndItemTypeAndIsActiveTrue(branchId, itemType);
        //ca làm và số lần được book trong ngày
        var counts = inspectionScheduleRepository.countBookedByShiftOnDate(branchId, date, itemType, OCCUPIED);
        Map<Long, Long> bookedByShift = counts.stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1]
                ));

        //check lại
        var items = shifts.stream().map(s -> ShiftAvailabilityDTO.builder()
                        .shiftId(s.getId())
                        .code(s.getCode())
                        .name(s.getName())
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                //
                        .booked(bookedByShift.getOrDefault(s.getId(), 0L) > 0)
                        .build())
                .collect(Collectors.toList());

        res.setStatus(200);
        res.setSuccess(true);
        res.setMessage("OK");
        res.setData(items);
        return res;
    }

    public boolean isTemplateInUse(Long templateId) {
        return inspectionScheduleRepository.existsByShift_IdAndStatusIn(templateId, OCCUPIED);
    }
}
