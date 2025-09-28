package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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



}
