package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.ListingDetailResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.ListingReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Listing;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ListingMedia;

import java.util.List;

public interface ListingService {
   BaseResponse<List<ListingReponseDTO>> getAllListings(int pageSize, int pageNumber);
   //BaseResponse<ListingDetailResponseDTO> getListingById(long id);
}
