package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.AcceptedConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.RejectedConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.AccountRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.ConsignmentRequestListItemDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentRequest.ConsignmentRequestService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing.ListingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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


    @PutMapping("/consignment-request/consider_accepted")
    public ResponseEntity<BaseResponse<Void>> requestAccepted(@RequestBody @Valid AcceptedConsignmentRequestDTO dto) {
        BaseResponse<Void> response = consignmentRequestService.RequestAccepted(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/consignment-request/consider_rejected")
    public ResponseEntity<BaseResponse<Void>> requestRejected(@RequestBody @Valid RejectedConsignmentRequestDTO dto) {
        BaseResponse<Void> response = consignmentRequestService.RequestRejected(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //lấy tất cả request có staff
    @GetMapping("/consignment-request")
    public ResponseEntity<BaseResponse<PageResponse<ConsignmentRequestListItemDTO>>> getStaffList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "desc") String dir
    ){
        BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> response = consignmentRequestService.getListByStaffId(page, size, dir, sort);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //lấy tất cả request staff nhưng chưa duyệt
    @GetMapping("/consignment-request/consider")
    public ResponseEntity<BaseResponse<PageResponse<ConsignmentRequestListItemDTO>>> getStaffListForConsider(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "desc") String dir
    ){
        BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> response = consignmentRequestService.getListByStaffIdAndNotConsider(page, size, dir, sort);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
