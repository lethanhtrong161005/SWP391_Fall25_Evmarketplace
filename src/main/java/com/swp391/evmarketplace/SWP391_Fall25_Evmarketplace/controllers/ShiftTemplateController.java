package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.shift.CreateShiftTemplateDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.shift.UpdateShiftTemplateDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftTemplateResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftAvailabilityDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.shift.ShiftAvailabilityService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.shift.ShiftTemplateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shifts/templates")
public class ShiftTemplateController {

    @Autowired private ShiftTemplateService shiftTemplateService;
    @Autowired private ShiftAvailabilityService shiftAvailabilityService;

    @PostMapping("/")
    public ResponseEntity<BaseResponse<ShiftTemplateResponseDTO>> create(@Valid @RequestBody CreateShiftTemplateDTO dto) {
        var res = shiftTemplateService.create(dto);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<PageResponse<ShiftTemplateResponseDTO>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        var res = shiftTemplateService.list(page, size, sort, dir);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ShiftTemplateResponseDTO>> update(@PathVariable Long id,
                                                               @RequestBody UpdateShiftTemplateDTO dto) {
        var res = shiftTemplateService.update(id, dto);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<?>> delete(@PathVariable Long id) {
        var res = shiftTemplateService.delete(id);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/availability")
    public ResponseEntity<BaseResponse<java.util.List<ShiftAvailabilityDTO>>> getAvailability(
            @RequestParam Long branchId,
            @RequestParam ItemType itemType,
            @RequestParam java.time.LocalDate date
    ) {
        var res = shiftAvailabilityService.getAvailability(branchId, itemType, date);
        return ResponseEntity.status(res.getStatus()).body(res);
    }
}
