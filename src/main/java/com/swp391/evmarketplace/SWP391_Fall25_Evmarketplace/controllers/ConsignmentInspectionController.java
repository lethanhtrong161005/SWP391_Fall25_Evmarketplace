package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspection.CreateInspectionDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentInspectionResult;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentInspectionProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentInspection.ConsignmentInspectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/inspections")
public class ConsignmentInspectionController {
    @Autowired
    private ConsignmentInspectionService inspectionService;

    @PostMapping("/add")
    public ResponseEntity<BaseResponse<Void>> createInspection(
            @Valid @RequestBody CreateInspectionDTO dto
    ) {
        BaseResponse<Void> res = inspectionService.createInspection(dto);
        // 200 OK là ổn, nếu bạn muốn 201 CREATED thì đổi ở đây
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    //    Vô hiệu hóa inspection hiện tại (set isActive = false)
//    Chỉ cho phép nếu request chưa ký hợp đồng (SIGNED).
    @PutMapping("/{inspectionId}/inactive")
    public ResponseEntity<BaseResponse<Void>> inactiveInspection(
            @PathVariable Long inspectionId
    ) {
        BaseResponse<Void> res = inspectionService.inactiveInspection(inspectionId);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


//    Lấy inspection đang active của 1 request
    @GetMapping("/request/{requestId}")
    public ResponseEntity<BaseResponse<ConsignmentInspectionProjection>> getInspectionByRequestId(
            @PathVariable Long requestId
    ) {
        BaseResponse<ConsignmentInspectionProjection> res =
                inspectionService.getInspectionByRequestId(requestId);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

//    Lọc danh sách inspections theo status và isActive.
//  manager
    @GetMapping("/")
    public ResponseEntity<BaseResponse<List<ConsignmentInspectionProjection>>> findAllViewsByStatus(
            @RequestParam(value = "status", required = false)
            Collection<ConsignmentInspectionResult> statuses,

            @RequestParam(value = "isActive", required = false)
            Boolean isActive
    ) {
        BaseResponse<List<ConsignmentInspectionProjection>> res =
                inspectionService.findAllViewsByStatus(statuses, isActive);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


    //
    @GetMapping("/staff/all")
    public ResponseEntity<BaseResponse<List<ConsignmentInspectionProjection>>> getListInspectionByStaffId() {
        BaseResponse<List<ConsignmentInspectionProjection>> res = inspectionService.getListInspectionByStaffId();
        return ResponseEntity.status(res.getStatus()).body(res);
    }

}
