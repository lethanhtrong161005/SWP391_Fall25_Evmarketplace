package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.ConsignmentListingFilter;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.CreateListingRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.UpdateListingRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.CategoryCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ListingStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ListingListProjection;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ListingService {
    BaseResponse<CreateListingResponse> createListing(CreateListingRequest req, List<MultipartFile> images, List<MultipartFile> videos);

    BaseResponse<PageResponse<ListingListItemDTO>> getMyListings(ListingStatus status, String q, Integer page, Integer size);

    Map<ListingStatus, Long> getMyCounts(Long sellerId);

    BaseResponse<PageResponse<ListingCardDTO>> searchForPublic(SearchListingRequestDTO requestDTO,
                                                               int page, int size, String sort, String dir);

    BaseResponse<PageResponse<ListingCardDTO>> searchForManage(SearchListingRequestDTO requestDTO,
                                                               int page, int size, String sort, String dir);

    BaseResponse<Map<String, Object>> getAllListForModerator(int page, int size, String sort, String dir);

    BaseResponse<PageResponse<ListingCardDTO>> getAllListingsPublic(
            String type, CategoryCode categoryCode, String status,
            int page, int size, String sort, String dir
    );


    //Lấy chi tiết bài đăng theo người bán
    BaseResponse<ListingDetailResponseDto> getListingDetailBySeller(Long listingId, Long sellerId);

    //Lấy chi tiết bài đăng
    BaseResponse<ListingDetailResponseDto> getListingDetailById(Long listingId);
    //Chỉnh Bài Đăng

    /**
     * Chỉnh sửa thông tin bài đăng ở trạng thái PENDING, REJECTED, REJECTED đối với ROLE MEMBER
     * - Nếu như data rỗng hoặc null sẽ giữa data cũ.
     * - Không chỉnh sửa danh mục của bài đăng.
     * - Chỉ chủ đăng bài mới được chỉnh bài đăng
     * - Nếu tin normal muốn gia hạn thì cần 7 ngày để gia hạn
     * <p>
     * *
     */
    BaseResponse<?> updatedListing(
            Long id,
            Long sellerId,
            UpdateListingRequest req,
            List<MultipartFile> images,
            List<MultipartFile> videos,
            List<Long> keepMediaIds
    );

    //Xoá bài đăng

    /**
     * Chỉ xoá bài đăng ở trạng thái PENDING đối ROLE MEMBER
     **/
    BaseResponse<?> deleteListing(Long listingId);

    //Thay đổi trạng thái bài đăng

    /**
     *
     **/
    BaseResponse<?> changeStatus(Long listingId, ListingStatus newStatus);

    BaseResponse<?> restore(Long listingId);

    BaseResponse<?> claim(Long listingId, Long actorId, boolean force);

    BaseResponse<?> extend(Long listingId, Long actorId);

    BaseResponse<?> release(Long listingId, Long actorId, boolean force);

    List<Map<String, Object>> myActiveLocks(Long actorId, String rawTitle);

    BaseResponse<?> approve(Long listingId, Long actorId, boolean force);

    BaseResponse<?> reject(Long listingId, Long actorId, String reason, boolean force);

    BaseResponse<Map<String, Object>> getQueuePaged(
            ListingStatus status, int pageIdx, int pageSize, String rawTitle
    );

    BaseResponse<PageResponse<ListingHistoryDto>> getModeratorHistory(
            Long actorId,
            String q,
            LocalDateTime fromTs,
            LocalDateTime toTs,
            List<String> reasons,
            Set<ListingStatus> toStatuses,
            Integer page,
            Integer size
    );

    /**
     * Lấy bài đăng do staff quản lý
     **/
    BaseResponse<?> getListingAllByStaffId(Long staffId);

    //Consignment Listing
    BaseResponse<?> createListingConsignment(CreateListingRequest req, List<MultipartFile> images, List<MultipartFile> videos);

    BaseResponse<?> searchConsignment(ConsignmentListingFilter f, int page, int size);


    BaseResponse<?> updateConsignmentListing(
            Long id,
            UpdateListingRequest req,
            List<MultipartFile> images,
            List<MultipartFile> videos,
            List<Long> keepMediaIds
    );

    BaseResponse<?> managerListing(ListingStatus status, String q, int page, int size);
    BaseResponse<?> managerListingUpdate(Long listingId,  ListingStatus status);
}
