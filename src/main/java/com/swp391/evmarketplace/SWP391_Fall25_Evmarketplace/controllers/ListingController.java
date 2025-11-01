package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.ChangeStatusRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.CreateListingRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.UpdateListingRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingCardDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.CategoryCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ListingListProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing.ListingService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/listing")
public class ListingController {
    @Autowired
    private ListingService listingService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthUtil authUtil;

    //TYPE: VEHICLE, BATTERY
    @GetMapping("/")
    public ResponseEntity<BaseResponse<PageResponse<ListingCardDTO>>> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) CategoryCode categoryCode,
            @RequestParam(required = false) ListingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "desc") String dir
    ) {
        status = status != null ? status : ListingStatus.ACTIVE;
        BaseResponse<PageResponse<ListingCardDTO>> res =
                listingService.getAllListingsPublic(type, categoryCode, status.name(), page, size, sort, dir);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


    @PostMapping(value = "/post",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> postListing(
            @RequestPart("payload") String payload,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "videos", required = false) List<MultipartFile> videos
    ) {
        try {
            CreateListingRequest req = objectMapper.readValue(payload, CreateListingRequest.class);
            var res = listingService.createListing(req, images, videos);
            return ResponseEntity.status(res.getStatus()).body(res);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<PageResponse<ListingCardDTO>>> searchCards(
            @ModelAttribute SearchListingRequestDTO requestDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "desc") String dir
    ) {
        BaseResponse<PageResponse<ListingCardDTO>> response = listingService
                .searchForPublic(requestDTO, page, size, sort, dir);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getMine(
            @RequestParam(required = false) ListingStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        var res = listingService.getMyListings(status, q, page, size);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/mine/counts")
    public BaseResponse<Map<ListingStatus, Long>> getMyCounts() {
        Long sellerId = authUtil.getCurrentAccount().getId();
        var data = listingService.getMyCounts(sellerId);

        var res = new BaseResponse<Map<ListingStatus, Long>>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("OK");
        res.setData(data);
        return res;
    }

    @PutMapping(value = "/{listingId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> updateListing(
            @PathVariable Long listingId,
            @RequestParam("payload") String payloadJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "videos", required = false) List<MultipartFile> videos,
            @RequestParam(value = "keepMediaIds", required = false) List<Long> keepMediaIds // <= String->Long binding
    ) {
        try {
            UpdateListingRequest req = objectMapper.readValue(payloadJson, UpdateListingRequest.class);
            var res = listingService.updatedListing(
                    listingId, authUtil.getCurrentAccount().getId(),
                    req, images, videos, keepMediaIds == null ? List.of() : keepMediaIds
            );
            return ResponseEntity.status(res.getStatus()).body(res);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false, "status", 400, "message", "Invalid JSON in 'payload'", "detail", e.getOriginalMessage()
            ));
        }
    }

    @DeleteMapping("/delete/{listingId}")
    public ResponseEntity<?> deleteListing(@PathVariable Long listingId) {
        var res = listingService.deleteListing(listingId);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    //Lấy chi tiết bài đăng theo người đăng
    @GetMapping("/seller/{listingId}")
    public ResponseEntity<?> getListingBySeller(@PathVariable Long listingId) {
        var res = listingService.getListingDetailBySeller(listingId, authUtil.getCurrentAccount().getId());
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    //Lấy chi tiết bài đăng
    @GetMapping("/{listingId}")
    public ResponseEntity<?> getListingById(@PathVariable Long listingId) {
        var res = listingService.getListingDetailById(listingId);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    //Thay đổi trạng thái bài đăng
    @PostMapping("/status/change")
    public ResponseEntity<?> changeListingStatus(@Valid @RequestBody ChangeStatusRequest request) {
        var res = listingService.changeStatus(request.getId(), request.getStatus());
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    //Restore
    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreListing(@PathVariable Long id) {
        var res = listingService.restore(id);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


}
