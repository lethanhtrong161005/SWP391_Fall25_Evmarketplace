package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.CreateListingRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingReponseDTO;

import java.util.List;

public interface ListingService {
   BaseResponse<List<ListingReponseDTO>> getAllListings(int pageSize, int pageNumber);
   //BaseResponse<ListingDetailResponseDTO> getListingById(long id);

   BaseResponse<Void> createListing(CreateListingRequest req);
}
