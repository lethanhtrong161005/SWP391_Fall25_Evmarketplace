package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import ch.qos.logback.core.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.CreateConsignmentRequestByStaffDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.RejectListingRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ErrorCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.AccountRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.ConsignmentRequestListItemDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentRequest.ConsignmentRequestService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing.ListingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
@Tag(name = "staff", description = "APIs that staff manage")
public class StaffController {

    @Autowired
    private ListingService listingService;
    @Autowired
    private ConsignmentRequestService consignmentRequestService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ObjectMapper objectMapper;


    //consignment request

    @PostMapping(value = "/consignment-request/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestPart("payload") String payload,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "videos", required = false) List<MultipartFile> videos
    ) {
        //check acc

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CreateConsignmentRequestByStaffDTO req = objectMapper.readValue(payload, CreateConsignmentRequestByStaffDTO.class);

            if (StringUtil.isNullOrEmpty(req.getPhone()))
                throw new CustomBusinessException(ErrorCode.PHONE_REQUIRED.name());
            Account owner = accountRepository.findByPhoneNumber(req.getPhone())
                    .orElseThrow(() -> new CustomBusinessException(ErrorCode.ACCOUNT_NOT_FOUND.name()));

            BaseResponse<Void> res = consignmentRequestService.createConsignmentRequest(req, owner, images, videos);
            return ResponseEntity.status(res.getStatus()).body(res);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/consignment-request/all")
    public ResponseEntity<BaseResponse<PageResponse<ConsignmentRequestListItemDTO>>> getAllConsignmentRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir
    ) {
        BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> response = consignmentRequestService.getAll(page, size, dir, sort);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
