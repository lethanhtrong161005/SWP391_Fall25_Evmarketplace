package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.CreateConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.UpdateSetScheduleRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentRequestProject;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.ConsignmentRequestService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consignments")
public class ConsignmentController {

    @Autowired
    private ConsignmentRequestService consignmentRequestService;
    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/add")
    public ResponseEntity<BaseResponse<Void>> create(@RequestBody @Valid CreateConsignmentRequestDTO req) {
        Account account = authUtil.getCurrentAccount();
        BaseResponse<Void> res = consignmentRequestService.createConsignmentRequest(req, account);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/")
    public ResponseEntity<BaseResponse<PageResponse<ConsignmentRequestProject>>> getMineListRequest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir
    ){
        Long id = authUtil.getCurrentAccount().getId();
        BaseResponse<PageResponse<ConsignmentRequestProject>> response = consignmentRequestService.getListById( id, page, size, dir, sort);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/set-schedule")
    public ResponseEntity<?> setSchedule(@Valid @RequestBody UpdateSetScheduleRequestDTO dto){
        BaseResponse<Void> response = consignmentRequestService.setRequestSchedule(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
