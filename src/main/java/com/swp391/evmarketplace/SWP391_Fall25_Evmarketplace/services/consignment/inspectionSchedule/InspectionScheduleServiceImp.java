package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.inspectionSchedule;

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

    private static final int GRACE_MINUTES = 15;
    //kiểm tra ca này giờ này có thể book không
    private boolean isShiftSelectableToday(LocalDate date, LocalTime start, LocalTime end) {
        if (!date.isEqual(LocalDate.now())) return true;
        var now = LocalTime.now();
        if (now.isAfter(end)) return false;                         // đã qua ca
        if (now.plusMinutes(GRACE_MINUTES).isAfter(start)) return false; // quá sát giờ
        return true;
    }

    @Override
    public BaseResponse<Void> createSchedule(CreateInspectionScheduleDTO dto) {
        Account account = authUtil.getCurrentAccount();

        ConsignmentRequest request = consignmentRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name()));

        //check owner của request
        if(!request.getOwner().getId().equals(account.getId()))
            throw new CustomBusinessException("You don't have permission");

        LocalDate date = dto.getDate();
        String note = null;

        if (dto.getNote() != null && !(dto.getNote().trim().isEmpty())) {
            note = dto.getNote();
        }

        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) throw new CustomBusinessException("DATE_PAST_NOT_ALLOWED");

        if (date.isAfter(today.plusDays(10))) throw new CustomBusinessException("DATE_EXCEEDS_10_DAYS_LIMIT");

        ShiftTemplate template = shiftTemplateRepository.findById(dto.getShiftId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.SHIFT_TEMPLATE_NOT_FOUND.name()));

        Account staff = request.getStaff();
        Branch branch = request.getPreferredBranch();

        if(!request.getItemType().equals(template.getItemType())) throw new CustomBusinessException("TEMPLATE_NOT_FOR_THIS_TYPE");


        //validation
        if (!template.getBranch().getId().equals(branch.getId())) {
            throw new CustomBusinessException("SHIFT_NOT_IN_BRANCH");
        }
        if (!template.getIsActive()) {
            throw new CustomBusinessException("SHIFT_INACTIVE");
        }

        //1 request 1 schedule
        var existing = inspectionScheduleRepository
                .findFirstByRequest_IdAndStatusInOrderByIdDesc(staff.getId(),
                        List.of(InspectionScheduleStatus.SCHEDULED, InspectionScheduleStatus.CHECKED_IN));
        if (existing.isPresent()) throw new CustomBusinessException("REQUEST_ALREADY_HAS_ACTIVE_SCHEDULE");

        //staff có bận không
        boolean busy = inspectionScheduleRepository
                .existsByStaff_IdAndShift_IdAndScheduleDateAndStatusIn(staff.getId(), dto.getShiftId(), date,
                        List.of(InspectionScheduleStatus.SCHEDULED, InspectionScheduleStatus.CHECKED_IN));
        if (busy) throw new CustomBusinessException("STAFF_BUSY_THIS_SLOT");


        InspectionSchedule schedule = new InspectionSchedule();
        schedule.setRequest(request);
        schedule.setStaff(staff);
        schedule.setBranch(branch);
        schedule.setScheduleDate(date);
        schedule.setShift(template);
        schedule.setStatus(InspectionScheduleStatus.SCHEDULED);
        schedule.setScheduledBy(account);
        schedule.setNote(note);
        //request
        request.setStatus(ConsignmentRequestStatus.SCHEDULED);
        request.setStatusChangeAt(LocalDateTime.now());

        try {
            inspectionScheduleRepository.saveAndFlush(schedule);
        } catch (DataIntegrityViolationException e){
            throw new CustomBusinessException("SLOT_JUST_TAKEN");
        }

        BaseResponse<Void> response = new BaseResponse<>();
        response.setMessage("Created");
        response.setSuccess(true);
        response.setStatus(200);
        return response;

    }



    private static final List<InspectionScheduleStatus> OCCUPIED = List.of(
            InspectionScheduleStatus.SCHEDULED,
            InspectionScheduleStatus.CHECKED_IN
    );

    //lấy danh sách ca làm trống
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




}
