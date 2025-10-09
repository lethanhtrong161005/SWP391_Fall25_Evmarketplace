package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import ch.qos.logback.core.util.StringUtil;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.CreateConsignmentRequestByStaffDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ErrorCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.AccountRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentRequestProject;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.ConsignmentRequestService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing.ListingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    //líting
    @GetMapping("/listing")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getAllList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "desc") String dir
    ) {
        BaseResponse<Map<String, Object>> response = listingService.getAllListForManage(page, size, sort, dir);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/listing/search")
    public ResponseEntity<BaseResponse<Map<String, Object>>> searchCards(
            @ModelAttribute SearchListingRequestDTO requestDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "desc") String dir
    ) {
        BaseResponse<Map<String, Object>> response = listingService.searchForManage(requestDTO, page, size, sort, dir);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //consignment request

    @PostMapping("/consignment-request/add")
    public ResponseEntity<BaseResponse<Void>> create(@RequestBody @Valid CreateConsignmentRequestByStaffDTO req) {
        if(StringUtil.isNullOrEmpty(req.getPhone())) throw new CustomBusinessException(ErrorCode.PHONE_REQUIRED.name());
        Account owner = accountRepository.findByPhoneNumber(req.getPhone())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.ACCOUNT_NOT_FOUND.name()));

        BaseResponse<Void> res = consignmentRequestService.createConsignmentRequest(req, owner);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/consignment-request")
    public ResponseEntity<BaseResponse<PageResponse<ConsignmentRequestProject>>> getAllConsignmentRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir
    ) {
        BaseResponse<PageResponse<ConsignmentRequestProject>> response = consignmentRequestService.getAll(page, size, dir, sort);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
