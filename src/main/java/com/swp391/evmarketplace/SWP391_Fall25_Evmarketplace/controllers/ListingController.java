package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<Map<String, Object>>> searchCards(@ModelAttribute SearchListingRequestDTO requestDTO) {
        BaseResponse<Map<String, Object>> response = listingService.searchCard(requestDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }




}
