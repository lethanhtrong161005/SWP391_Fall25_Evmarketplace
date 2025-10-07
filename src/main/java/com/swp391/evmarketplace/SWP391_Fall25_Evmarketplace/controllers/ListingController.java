package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.CreateListingRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing.ListingService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AuthUtil authUtil;

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "desc") String dir
    ) {
        BaseResponse<Map<String, Object>> response = listingService.getAllListingsPublic(page, size, sort, dir);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(value="/post", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postListing(
            @RequestPart("payload") CreateListingRequest req,
            @RequestPart(value="images", required=false) List<MultipartFile> images,
            @RequestPart(value="videos", required=false) List<MultipartFile> videos
    ){
        try {
            // parse JSON → DTO
            var res = listingService.createListing(req, images, videos);
            return ResponseEntity.status(res.getStatus()).body(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<Map<String, Object>>> searchCards(
            @ModelAttribute SearchListingRequestDTO requestDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "desc") String dir
    ) {
        BaseResponse<Map<String, Object>> response = listingService.searchForPublic(requestDTO, page, size, sort, dir);
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
}
