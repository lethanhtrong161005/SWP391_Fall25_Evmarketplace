package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.RejectListingRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Listing;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing.ListingService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/moderator")
public class ModeratorController {
    @Autowired
    private ListingService listingService;
    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/listing/{id}/claim")
    public ResponseEntity<?> claim(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean force //Đối với Role Manager thì dùng này để cứớp quyền
    ) {
        var res = listingService.claim(id, authUtil.getCurrentAccountIdOrNull(), force);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    // Gia hạn lock (FE ping mỗi 2–3 phút)
    @PostMapping("/listing/{id}/extend")
    public ResponseEntity<?> extend(
            @PathVariable Long id
    ) {
        var res = listingService.extend(id, authUtil.getCurrentAccountIdOrNull());
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    // Nhả lock – force=true cho phép giải phóng lock của người khác (quyền ở Security)
    @PostMapping("/listing/{id}/release")
    public ResponseEntity<?> release(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean force
    ) {
        var res = listingService.release(id, authUtil.getCurrentAccountIdOrNull(), force);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    // Queue
    @GetMapping("/listing/queue")
    public ResponseEntity<?> queue(
            @RequestParam(defaultValue = "PENDING") ListingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title
    ) {
        var res = listingService.getQueuePaged(status, page, size, title);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    // My locks
    @GetMapping("/listing/my-locks")
    public ResponseEntity<?> myLocks(
            @RequestParam(required = false) String title
    ) {
        List<Map<String, Object>> data = listingService.myActiveLocks(authUtil.getCurrentAccountIdOrNull(), title);
        var res = new BaseResponse<List<Map<String, Object>>>();
        res.setData(data);
        res.setStatus(200);
        res.setSuccess(true);
        res.setMessage("My locks");
        return ResponseEntity.ok(res);
    }

    @PutMapping("/listing/approve/{id}")
    public ResponseEntity<?> approveModeration(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean force
    ) {
        var res = listingService.approve(id, authUtil.getCurrentAccountIdOrNull(), force);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/listing/reject{id}")
    public ResponseEntity<?> rejectModeration(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean force,
            @Valid @RequestBody RejectListingRequest request
    ) {
        var res = listingService.reject(id, authUtil.getCurrentAccountIdOrNull(), request.getReason(), force);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getModeratorHistory(
            @RequestParam(required = false) Long actorId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromTs,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toTs,
            @RequestParam(required = false) List<String> reasons,
            @RequestParam(required = false) Set<ListingStatus> toStatuses,       
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        var res = listingService.getModeratorHistory(
                actorId, q, fromTs, toTs, reasons, toStatuses, page, size
        );
        return ResponseEntity.status(res.getStatus()).body(res);
    }


}
