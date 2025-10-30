package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.contract.ActivateContractRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.contract.CreateContractRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ContractSignMethod;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ContractStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.contract.ContractService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/contract")
public class ContractController {

    @Autowired
    private ContractService contractService;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createContract(
            @RequestPart("payload") String payload,
            @RequestPart("file")MultipartFile file,
            HttpServletRequest http
            ){

        try{
            CreateContractRequest createContractRequest = objectMapper.readValue(payload, CreateContractRequest.class);
            var res = contractService.createContract(createContractRequest, file, http);
            return ResponseEntity.status(res.getStatus()).body(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping(value = "/active", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> activateContract(
            @RequestBody @Valid ActivateContractRequest payload,
            HttpServletRequest http
    ){
        var res = contractService.activateContract(payload, http);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping
    public ResponseEntity<?> getAllContracts(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) ContractStatus status,
            @RequestParam(required = false) ContractSignMethod method,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long buyerId,
            @RequestParam(required = false) Long sellerId,

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime createdFrom,

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime createdTo,

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime signedFrom,

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime signedTo,

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime effectiveFrom,

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) LocalDateTime effectiveTo,

            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String dir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var res = contractService.getAllContracts(
                orderId,
                status,
                method,
                branchId,
                buyerId,
                sellerId,
                createdFrom,
                createdTo,
                signedFrom,
                signedTo,
                effectiveFrom,
                effectiveTo,
                q,
                sort,
                dir,
                page,
                size
        );
        return ResponseEntity.status(res.getStatus()).body(res);
    }



}
