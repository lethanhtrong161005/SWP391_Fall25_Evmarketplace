package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingReponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ListingService {
   BaseResponse<List<ListingReponseDTO>> getAllListings(int pageSize, int pageNumber);
   //BaseResponse<ListingDetailResponseDTO> getListingById(long id);

   //cho phép sort theo thời gian tạo
   BaseResponse<Map<String, Object>> searchCard(SearchListingRequestDTO requestDTO);
   BaseResponse<Map<String, Object>> getAllListForManage(int page, int size, String sort, String dir);
   BaseResponse<Map<String, Object>> getSellerList(Long id, int page, int size, String sort, String dir);
}
