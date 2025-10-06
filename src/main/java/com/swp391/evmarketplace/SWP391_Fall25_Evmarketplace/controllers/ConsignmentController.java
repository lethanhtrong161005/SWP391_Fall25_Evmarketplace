package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.CreateConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.ConsignmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consignments")
public class ConsignmentController {

    @Autowired
    private ConsignmentService consignmentService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> create(@RequestBody @Valid CreateConsignmentRequestDTO req) {
        BaseResponse<Void> res = consignmentService.createConsignmentRequest(req);
        return ResponseEntity.status(res.getStatus()).body(res);
    }
}
