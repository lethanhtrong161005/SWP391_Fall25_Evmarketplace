package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentSettlement;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentSettlement.ConsignmentSettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/consignment_settlement")
public class ConsignmentSettlementController {

    @Autowired
    private ConsignmentSettlementService consignmentSettlementService;

    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        var response = consignmentSettlementService.getAll();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{agreementId}")
    public ResponseEntity<?> getById(@PathVariable Long agreementId) {
        var response = consignmentSettlementService.getByAgreementId(agreementId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getListWithoutPayout() {
        var response = consignmentSettlementService.getListWithoutPayout();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(value = "/{settlementId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> setPayout(
            @PathVariable Long settlementId,
            @RequestPart(value = "images", required = false) MultipartFile file
    ) {
        var response = consignmentSettlementService.setPayout(settlementId, file);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
