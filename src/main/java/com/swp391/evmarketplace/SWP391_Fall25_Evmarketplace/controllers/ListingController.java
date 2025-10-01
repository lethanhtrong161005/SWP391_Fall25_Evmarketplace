package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.CreateListingRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing.ListingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/listing")
public class ListingController {
    @Autowired
    private ListingService listingService;

    @GetMapping("/all")
    public ResponseEntity<?> getListings(int pageSize, int pageNumber) {
        BaseResponse<List<ListingReponseDTO>> response = listingService.getAllListings(pageSize, pageNumber);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postListing(@Valid @ModelAttribute CreateListingRequest request) {
        var res = listingService.createListing(request);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<Map<String, Object>>> searchCards(@ModelAttribute SearchListingRequestDTO requestDTO) {
        BaseResponse<Map<String, Object>> response = listingService.searchCard(requestDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }



}
