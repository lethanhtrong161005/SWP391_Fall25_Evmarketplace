package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.CreateListingRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.CreateListingResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingDetailResponseDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingListItemDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Map;

public interface ListingService {
  BaseResponse<CreateListingResponse> createListing(CreateListingRequest req, List<MultipartFile> images, List<MultipartFile> videos);
  BaseResponse<PageResponse<ListingListItemDTO>> getMyListings(ListingStatus status, String q, Integer page, Integer size);
  Map<ListingStatus, Long> getMyCounts(Long sellerId);
   //cho phép sort theo thời gian tạo
   BaseResponse<Map<String, Object>> searchForPublic(SearchListingRequestDTO requestDTO, int page, int size, String sort, String dir);
  BaseResponse<Map<String, Object>> searchForManage(SearchListingRequestDTO requestDTO, int page, int size, String sort, String dir);
   BaseResponse<Map<String, Object>> getAllListForManage(int page, int size, String sort, String dir);
   BaseResponse<Map<String, Object>> getAllListingsPublic(int page, int size, String sort, String dir);

   //Thanh toán bài đăng
   BaseResponse<String> createPromotionPaymentUrl(Long listingId, HttpServletRequest request);


    //Lấy chi tiết bài đăng theo người bán
    BaseResponse<ListingDetailResponseDto> getListingDetailBySeller(Long listingId, Long sellerId);

    //Lấy chi tiết bài đăng
    BaseResponse<ListingDetailResponseDto> getListingDetailById(Long listingId);
}
