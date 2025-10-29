package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.agree.CreateAgreementDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentAgreement;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AgreementDuration;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentAgreementProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentAgreement.ConsignmentAgreementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/agreements")
public class ConsignmentAgreementController {
    @Autowired
    ConsignmentAgreementService agreementService;
    @Autowired
    ObjectMapper objectMapper;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<Void>> createAgreement(
            @RequestPart("payload") String payload,
            @RequestPart(value = "file") MultipartFile file
    ) throws JsonProcessingException {
        CreateAgreementDTO dto = objectMapper.readValue(payload, CreateAgreementDTO.class);
        BaseResponse<Void> res = agreementService.createAgreement(dto, file);
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
