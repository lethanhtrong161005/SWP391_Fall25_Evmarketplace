package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.agreement.ConsignmentAgreementDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.request.ConsignmentRequestListItemDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentAgreementProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.account.AccountService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentAgreement.ConsignmentAgreementService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentRequest.ConsignmentRequestService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing.ListingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@Tag(name = "manager", description = "APIs that manager manage")
public class ManagerController {
    @Autowired
    private ConsignmentRequestService consignmentRequestService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ConsignmentAgreementService consignmentAgreementService;
    @Autowired
    private ListingService listingService;

    //account
    @GetMapping("/accounts/staff")
    public ResponseEntity<BaseResponse<List<Account>>> getListStaffAccountInBranch() {
        BaseResponse<List<Account>> response = accountService.getStaffListInBranch();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //consignment
    @GetMapping("/consignment-request")
    public ResponseEntity<BaseResponse<PageResponse<ConsignmentRequestListItemDTO>>> getAllConsignmentRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir
    ) {
        BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> response = consignmentRequestService.getAll(page, size, dir, sort);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/branches/{branchId}/consignment-request/ignore-submitted")
    public ResponseEntity<BaseResponse<PageResponse<ConsignmentRequestListItemDTO>>> getAllByBranchIdIgnoreSubmitted(
            @PathVariable Long branchId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir
    ) {
        BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> response = consignmentRequestService.getAllByBranchIdIgnoreSubmitted(branchId, page, size, dir, sort);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/branches/{branchId}/consignment-requests/assign")
    public ResponseEntity<BaseResponse<List<ConsignmentRequestListItemDTO>>> getListRequestForAssign(@PathVariable Long branchId) {
        BaseResponse<List<ConsignmentRequestListItemDTO>> response = consignmentRequestService.getAllByBranchIdAndSubmitted(branchId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/consignment-requests/{requestId}/assign/{staffId}")
    public ResponseEntity<BaseResponse<Void>> setStaffForRequest(@PathVariable Long requestId, @PathVariable Long staffId) {
        BaseResponse<Void> response = consignmentRequestService.setStaffForRequest(requestId, staffId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //agreement
    @GetMapping("/agreements/all")
    public ResponseEntity<BaseResponse<List<ConsignmentAgreementDTO>>> getAll() {
        BaseResponse<List<ConsignmentAgreementDTO>> res =
                consignmentAgreementService.getAllAgreements();
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/listing")
    public ResponseEntity<?> getAllListing(
            @RequestParam(required = false) ListingStatus status,
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var res = listingService.managerListing(status, q, page, size);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/listing/{id}")
    public ResponseEntity<?> updateListing(
            @PathVariable Long id,
            @RequestParam(required = false) ListingStatus status
    ){
        var res = listingService.managerListingUpdate(id, status);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


}
