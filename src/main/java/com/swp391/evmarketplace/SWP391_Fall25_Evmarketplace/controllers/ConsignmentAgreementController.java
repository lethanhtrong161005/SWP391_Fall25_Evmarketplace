package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.agree.CreateAgreementDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentAgreement;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AgreementDuration;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentAgreementProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentAgreement.ConsignmentAgreementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agreements")
public class ConsignmentAgreementController {
    @Autowired
    ConsignmentAgreementService agreementService;

    @PostMapping("/add")
    public ResponseEntity<BaseResponse<Void>> createAgreement(
            @Valid @RequestBody CreateAgreementDTO dto
    ) {
        BaseResponse<Void> res = agreementService.createAgreement(dto);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<BaseResponse<ConsignmentAgreementProjection>> getByRequest(
            @PathVariable Long requestId
    ) {
        BaseResponse<ConsignmentAgreementProjection> res =
                agreementService.getAgreementByRequestId(requestId);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<BaseResponse<Void>> cancelAgreement(Long id) {
        BaseResponse<Void> res = agreementService.cancelAgreement(id);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/extend/{id}")
    public ResponseEntity<BaseResponse<Void>> updateAgreement(
            @PathVariable Long id,
            @RequestParam AgreementDuration duration
    ) {
        BaseResponse<Void> res = agreementService.updateAgreement(id, duration);
        return ResponseEntity.status(res.getStatus()).body(res);
    }
}
