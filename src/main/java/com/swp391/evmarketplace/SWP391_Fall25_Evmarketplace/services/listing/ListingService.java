package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.CreateListingRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Map;

public interface ListingService {
 BaseResponse<Void> createListing(CreateListingRequest req, List<MultipartFile> images, List<MultipartFile> videos);

   //cho phép sort theo thời gian tạo
   BaseResponse<Map<String, Object>> searchForPublic(SearchListingRequestDTO requestDTO, int page, int size, String sort, String dir);
  BaseResponse<Map<String, Object>> searchForManage(SearchListingRequestDTO requestDTO, int page, int size, String sort, String dir);
   BaseResponse<Map<String, Object>> getAllListForManage(int page, int size, String sort, String dir);
   BaseResponse<Map<String, Object>> getSellerList(Long id, int page, int size, String sort, String dir);
   BaseResponse<Map<String, Object>> getAllListingsPublic(int page, int size, String sort, String dir);
}
