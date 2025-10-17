package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.ConsignmentRequestListItemDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.account.AccountService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentRequest.ConsignmentRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/manager")
@Tag(name = "manager", description = "APIs that manager manage")
public class ManagerController {
    @Autowired
    ConsignmentRequestService consignmentRequestService;
    @Autowired
    AccountService accountService;

    //account
    @GetMapping("/branches/{branchId}/accounts/staff/")
    public ResponseEntity<BaseResponse<List<Account>>> getListStaffAccountInBranch(@PathVariable Long branchId) {
        BaseResponse<List<Account>> response = accountService.getStaffListInBranch(branchId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //consignment
    @GetMapping("/consignment-request/")
    public ResponseEntity<BaseResponse<PageResponse<ConsignmentRequestListItemDTO>>> getAllConsignmentRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir
    ) {
        BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> response = consignmentRequestService.getAll(page, size, dir, sort);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/branches/{branchId}/consignment-requests/assign")
    public ResponseEntity<BaseResponse<List<ConsignmentRequestListItemDTO>>> getListRequestForAssign(@PathVariable Long branchId) {
        BaseResponse<List<ConsignmentRequestListItemDTO>> response = consignmentRequestService.getListByBranchIdAndStaffIsNull(branchId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/consignment-requests/{requestId}/assign/{staffId}")
    public ResponseEntity<BaseResponse<Void>> setStaffForRequest(@PathVariable Long requestId, @PathVariable Long staffId){
        BaseResponse<Void> response = consignmentRequestService.setStaffForRequest(requestId, staffId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
