package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspectionSchedule.CancelScheduleDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspectionSchedule.CreateInspectionScheduleDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftAvailabilityDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftAvailabilityDayDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.InspectionScheduleStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.InspectionScheduleDetailProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.StaffScheduleRow;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.inspectionSchedule.InspectionScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/api/inspection_schedule")
public class InspectionScheduleController {
    @Autowired
    InspectionScheduleService inspectionScheduleService;

    @GetMapping("/availability")
    public ResponseEntity<BaseResponse<ShiftAvailabilityDayDTO>> getAvailability(
            @RequestParam Long staffId,
            @RequestParam Long branchId,
            @RequestParam ItemType itemType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        var res = inspectionScheduleService.getAvailability(staffId, branchId, itemType, date);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PostMapping("/booking")
    public ResponseEntity<BaseResponse<Void>> bookingInspection(@RequestBody CreateInspectionScheduleDTO dto) {
        BaseResponse<Void> response = inspectionScheduleService.createSchedule(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BaseResponse<Void>> cancel(@PathVariable Long id, @RequestBody @Valid CancelScheduleDTO dto) {
        BaseResponse<Void> response = inspectionScheduleService.cancelSchedule(id, dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PatchMapping("/{id}/check_in")
    public ResponseEntity<BaseResponse<Void>> checkin(@PathVariable Long id) {
        BaseResponse<Void> response = inspectionScheduleService.checkin(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @GetMapping("/inspection_schedule/{requestId}")
    public ResponseEntity<BaseResponse<List<InspectionScheduleDetailProjection>>> getDetailByScheduleId(
            @PathVariable Long requestId,
            @RequestParam(required = false) List<InspectionScheduleStatus> statuses
    ) {
        BaseResponse<List<InspectionScheduleDetailProjection>> response = inspectionScheduleService.getScheduleByRequestId(requestId, statuses);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


}
