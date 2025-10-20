package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspectionSchedule.CreateInspectionScheduleDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftAvailabilityDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftAvailabilityDayDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.inspectionSchedule.InspectionScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/inspection_schedule/")
public class InspectionScheduleController {
    @Autowired
    InspectionScheduleService inspectionScheduleService;

    @GetMapping("/availability")
    public ResponseEntity< BaseResponse<ShiftAvailabilityDayDTO>> getAvailability(
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
}
