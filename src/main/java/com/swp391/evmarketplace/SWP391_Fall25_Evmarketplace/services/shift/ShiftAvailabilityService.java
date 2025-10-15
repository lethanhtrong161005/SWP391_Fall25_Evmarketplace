package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.shift;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftAvailabilityDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftAvailabilityDayDTO;
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

                // validate date: not in the past and not over 10 days ahead
                LocalDate today = LocalDate.now();
                if (date.isBefore(today)) {
                        res.setStatus(400);
                        res.setSuccess(false);
                        res.setMessage("DATE_PAST_NOT_ALLOWED");
                        return res;
                }
                if (date.isAfter(today.plusDays(10))) {
                        res.setStatus(400);
                        res.setSuccess(false);
                        res.setMessage("DATE_EXCEEDS_10_DAYS_LIMIT");
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

        // Availability for a date range (inclusive)
        public BaseResponse<List<ShiftAvailabilityDayDTO>> getAvailabilityRange(Long branchId, ItemType itemType, LocalDate startDate, LocalDate endDate) {
                var res = new BaseResponse<List<ShiftAvailabilityDayDTO>>();
                if (branchId == null || itemType == null || startDate == null || endDate == null) {
                        res.setStatus(400);
                        res.setSuccess(false);
                        res.setMessage("BAD_REQUEST");
                        return res;
                }

                LocalDate today = LocalDate.now();
                if (startDate.isBefore(today)) {
                        res.setStatus(400);
                        res.setSuccess(false);
                        res.setMessage("START_DATE_PAST_NOT_ALLOWED");
                        return res;
                }
                if (endDate.isBefore(startDate)) {
                        res.setStatus(400);
                        res.setSuccess(false);
                        res.setMessage("END_BEFORE_START");
                        return res;
                }
                if (endDate.isAfter(today.plusDays(10))) {
                        res.setStatus(400);
                        res.setSuccess(false);
                        res.setMessage("END_DATE_EXCEEDS_10_DAYS_LIMIT");
                        return res;
                }

                List<ShiftAvailabilityDayDTO> days = new ArrayList<>();
                for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
                        var dayRes = getAvailability(branchId, itemType, d);
                        if (!dayRes.isSuccess()) {
                                // propagate first error
                                return new BaseResponse<List<ShiftAvailabilityDayDTO>>() {{
                                        setStatus(dayRes.getStatus());
                                        setSuccess(false);
                                        setMessage(dayRes.getMessage());
                                }};
                        }
                        days.add(ShiftAvailabilityDayDTO.builder()
                                        .date(d)
                                        .shifts(dayRes.getData())
                                        .build());
                }

                res.setStatus(200);
                res.setSuccess(true);
                res.setMessage("OK");
                res.setData(days);
                return res;
        }

    public boolean isTemplateInUse(Long templateId) {
        return inspectionScheduleRepository.existsByShift_IdAndStatusIn(templateId, OCCUPIED);
    }
}
