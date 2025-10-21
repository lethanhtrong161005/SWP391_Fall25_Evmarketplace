package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.inspectionSchedule;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspectionSchedule.CancelScheduleDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspectionSchedule.CreateInspectionScheduleDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftAvailabilityDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftAvailabilityDayDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentRequestStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ErrorCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.InspectionScheduleStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class InspectionScheduleServiceImp implements InspectionScheduleService {
    @Autowired
    InspectionScheduleRepository inspectionScheduleRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    BranchRepository branchRepository;
    @Autowired
    ConsignmentRequestRepository consignmentRequestRepository;
    @Autowired
    ShiftTemplateRepository shiftTemplateRepository;
    @Autowired
    AuthUtil authUtil;


    @Transactional
    @Override
    public BaseResponse<Void> createSchedule(CreateInspectionScheduleDTO dto) {
        Account account = authUtil.getCurrentAccount();

        validateDate(dto.getDate());

        ConsignmentRequest request = consignmentRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name()));

        //check owner của request
        if (!request.getOwner().getId().equals(account.getId()))
            throw new CustomBusinessException("You don't have permission");

        //note
        String note = null;
        if (dto.getNote() != null && !(dto.getNote().trim().isEmpty())) {
            note = dto.getNote();
        }

        //template
        ShiftTemplate template = shiftTemplateRepository.findById(dto.getShiftId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.SHIFT_TEMPLATE_NOT_FOUND.name()));

        //staff, branch
        Account staff = request.getStaff();
        Branch branch = request.getPreferredBranch();

        //type
        if (!request.getItemType().equals(template.getItemType()))
            throw new CustomBusinessException("TEMPLATE_NOT_FOR_THIS_TYPE");


        //validation
        if (!template.getBranch().getId().equals(branch.getId())) {
            throw new CustomBusinessException("SHIFT_NOT_IN_BRANCH");
        }
        if (!template.getIsActive()) {
            throw new CustomBusinessException("SHIFT_INACTIVE");
        }
        // is staff busy
        boolean busy = inspectionScheduleRepository
                .existsByStaff_IdAndShift_IdAndScheduleDateAndStatusIn(staff.getId(), dto.getShiftId(), dto.getDate(),
                        List.of(InspectionScheduleStatus.SCHEDULED, InspectionScheduleStatus.CHECKED_IN));
        if (busy) throw new CustomBusinessException("STAFF_BUSY_THIS_SLOT");

        boolean hasActive = inspectionScheduleRepository.existsByRequest_IdAndStatusIn(request.getId(), OCCUPIED);
        if (hasActive) throw new CustomBusinessException("REQUEST_ALREADY_HAS_ACTIVE_SCHEDULE");

        //check if book today
        validateShiftTimingForToday(dto.getDate(), template);

        InspectionSchedule schedule = new InspectionSchedule();
        schedule.setRequest(request);
        schedule.setStaff(staff);
        schedule.setBranch(branch);
        schedule.setScheduleDate(dto.getDate());
        schedule.setShift(template);
        schedule.setStatus(InspectionScheduleStatus.SCHEDULED);
        schedule.setScheduledBy(account);
        schedule.setNote(note);
        //request
        request.setStatus(ConsignmentRequestStatus.SCHEDULED);
        request.setStatusChangeAt(LocalDateTime.now());

        try {
            inspectionScheduleRepository.saveAndFlush(schedule);
            consignmentRequestRepository.save(request);
        } catch (DataIntegrityViolationException e) {
            throw new CustomBusinessException("REQUEST_ALREADY_HAS_ACTIVE_SCHEDULE");
        }

        BaseResponse<Void> response = new BaseResponse<>();
        response.setMessage("Created");
        response.setSuccess(true);
        response.setStatus(200);
        return response;

    }

    //lấy danh sách ca làm trống
    @Override
    public BaseResponse<ShiftAvailabilityDayDTO> getAvailability(Long staffId, Long branchId, ItemType itemType, LocalDate date) {
        if (branchId == null || itemType == null || date == null || staffId == null) {
            throw new CustomBusinessException("MISSING_PARAMS");
        }

        // validate date: not in the past and not over 10 days ahead
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) throw new CustomBusinessException("DATE_PAST_NOT_ALLOWED");
        //đặt lịch không hơn 10 ngày kể từ ngày đặt
        if (date.isAfter(today.plusDays(10))) throw new CustomBusinessException("DATE_EXCEEDS_10_DAYS_LIMIT");

        var shifts = shiftTemplateRepository.findByBranch_IdAndItemTypeAndIsActiveTrue(branchId, itemType);
        var items = shifts.stream()
                .map(s -> {
                    // ca trong ngày hôm đó có còn không
                    boolean selectable = isShiftSelectableToday(date, s.getStartTime(), s.getEndTime());
                    //staff có bận không
                    boolean isBusy = selectable && inspectionScheduleRepository
                            .existsByStaffIdAndShiftIdAndScheduleDateAndStatusIn(staffId, s.getId(), date, OCCUPIED);

                    boolean disable = !selectable || isBusy;
                    String reason = !selectable
                            ? "TIME_WINDOW_BLOCKED"   // đã qua ca hoặc quá sát giờ
                            : (isBusy ? "STAFF_BUSY" : null);

                    return ShiftAvailabilityDTO.builder()
                            .shiftId(s.getId())
                            .name(s.getName())
                            .code(s.getCode())
                            .shiftId(s.getId())
                            .code(s.getCode())
                            .name(s.getName())
                            .startTime(s.getStartTime())
                            .endTime(s.getEndTime())
                            .booked(isBusy)            // staff có bận/ không
                            .disable(disable)
                            .reason(reason)
                            .build();
                }).toList();

        ShiftAvailabilityDayDTO shiftAvailabilityDayDTO = new ShiftAvailabilityDayDTO(date, items);

        BaseResponse<ShiftAvailabilityDayDTO> response = new BaseResponse<>();
        response.setMessage("OK");
        response.setSuccess(true);
        response.setStatus(200);
        response.setData(shiftAvailabilityDayDTO);
        return response;
    }

    @Transactional
    @Override
    public BaseResponse<Void> cancelSchedule(Long scheduleID, CancelScheduleDTO dto) {
        Account account = authUtil.getCurrentAccount();

        var s = inspectionScheduleRepository.findById(scheduleID)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.NOT_FOUND_SCHEDULE.name()));

        Long currentId = account.getId();
        boolean isAuthorized = (s.getScheduledBy() != null && s.getScheduledBy().getId().equals(currentId))
                || (s.getStaff() != null && s.getStaff().getId().equals(currentId));

        if (!isAuthorized) {
            throw new CustomBusinessException("NO_PERMISSION_TO_CANCEL");
        }

        if (!s.getStatus().equals(InspectionScheduleStatus.SCHEDULED)) {
            throw new CustomBusinessException("CANNOT_CANCEL");
        }
        //no cancel when near meeting time
        ensureBeforeCutoff(s.getScheduleDate(), s.getShift().getStartTime());
        s.setStatus(InspectionScheduleStatus.CANCELLED);
        s.setCancelledAt(LocalDateTime.now());
        s.setCancelledReason(dto.getReason().trim());
        s.setCancelledBy(account);

        //request
        ConsignmentRequest request = s.getRequest();
        request.setStatus(ConsignmentRequestStatus.RESCHEDULED);

        //save
        inspectionScheduleRepository.saveAndFlush(s);
        consignmentRequestRepository.save(request);

        BaseResponse<Void> response = new BaseResponse<>();
        response.setStatus(200);
        response.setMessage("OK");
        response.setSuccess(true);
        return response;
    }

    @Override
    @Transactional
    //staff
    public BaseResponse<Void> checkin(Long scheduleId) {
        Account account = authUtil.getCurrentAccount();

        var s = inspectionScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.NOT_FOUND_SCHEDULE.name()));

        if (!account.getId().equals(s.getStaff().getId())) {
            throw new CustomBusinessException("You don't have permission");
        }

        if (s.getStatus() != InspectionScheduleStatus.SCHEDULED) {
            throw new CustomBusinessException("ONLY_SCHEDULED_CAN_CHECKIN");
        }

        s.setStatus(InspectionScheduleStatus.CHECKED_IN);
        ConsignmentRequest request = s.getRequest();
        request.setStatus(ConsignmentRequestStatus.INSPECTING);
        s.setCheckinAt(LocalDateTime.now());

        inspectionScheduleRepository.save(s);
        consignmentRequestRepository.save(request);

        BaseResponse<Void> response = new BaseResponse<>();
        response.setStatus(200);
        response.setMessage("OK");
        response.setSuccess(true);
        return response;
    }


    //    ================HELPER================
    //kiểm tra ca này giờ này có thể book không
    private boolean isShiftSelectableToday(LocalDate date, LocalTime start, LocalTime end) {
        if (!date.isEqual(LocalDate.now())) return true;
        var now = LocalTime.now();
        if (now.isAfter(end)) return false;                         // đã qua ca
        if (now.plusMinutes(CUTOFF_MINUTES).isAfter(start)) return false; // quá sát giờ
        return true;
    }


    private static final List<InspectionScheduleStatus> OCCUPIED = List.of(
            InspectionScheduleStatus.SCHEDULED,
            InspectionScheduleStatus.CHECKED_IN
    );

    private static final int CUTOFF_MINUTES = 60;


    private void ensureBeforeCutoff(LocalDate date, LocalTime start) {
        if (!date.isEqual(LocalDate.now())) return;
        if (LocalTime.now().plusMinutes(CUTOFF_MINUTES).isAfter(start))
            throw new CustomBusinessException("TOO_LATE_TO_CANCEL");
    }

    private void validateDate(LocalDate date) {
        var today = LocalDate.now();
        if (date.isBefore(today))
            throw new CustomBusinessException("DATE_PAST_NOT_ALLOWED");
        if (date.isAfter(today.plusDays(10)))
            throw new CustomBusinessException("DATE_EXCEEDS_10_DAYS_LIMIT");
    }

    private void validateShiftTimingForToday(LocalDate date, ShiftTemplate shift) {
        if (!date.isEqual(LocalDate.now())) return;
        var now = LocalTime.now();
        if (now.isAfter(shift.getEndTime()))
            throw new CustomBusinessException("SHIFT_ALREADY_PASSED");
        if (now.plusMinutes(CUTOFF_MINUTES).isAfter(shift.getStartTime()))
            throw new CustomBusinessException("TOO_CLOSE_TO_SHIFT");
    }

}
