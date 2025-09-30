package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingListProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.SearchListingResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ErrorCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ListingServiceImp implements ListingService {

    @Autowired
    private ListingRepository listingRepository;

    @Override
    public BaseResponse<List<ListingReponseDTO>> getAllListings(int pageSize, int pageNumber) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        List<ListingReponseDTO> listings = listingRepository.findAll(page)
                .stream().map(listing -> {
                    ListingReponseDTO dto = new ListingReponseDTO();
                    dto.setId(listing.getId());
                    dto.setTitle(listing.getTitle());
                    dto.setProductVehicleId(
                            listing.getProductVehicle() != null ? listing.getProductVehicle().getId() : null
                    );
                    dto.setProductBatteryId(
                            listing.getProductBattery() != null ? listing.getProductBattery().getId() : null
                    );
                    dto.setSellerId(listing.getSeller().getId());


                    dto.setBrand(listing.getBrand());
                    dto.setModel(listing.getModel());
                    dto.setYear(listing.getYear());

                    dto.setBatteryCapacityKwh(listing.getBatteryCapacityKwh());
                    dto.setSohPercent(listing.getSohPercent());
                    dto.setMileageKm(listing.getMileageKm());
                    dto.setColor(listing.getColor());
                    dto.setDescription(listing.getDescription());


                    dto.setPrice(listing.getPrice());
                    dto.setVerified(listing.getVerified());
                    dto.setStatus(listing.getStatus().name());
                    dto.setProvince(listing.getProvince());
                    dto.setCity(listing.getCity());
                    dto.setAddress(listing.getAddress());
                    dto.setPromotedUntil(listing.getPromotedUntil());
                    dto.setThumbnail(
                            listing.getMediaList() != null && !listing.getMediaList().isEmpty()
                                    ? listing.getMediaList().get(0).getMediaUrl() : null
                    );
                    dto.setBranchId(
                            listing.getBranch() != null ? listing.getBranch().getId() : null
                    );
                    dto.setConsigned(listing.getConsigned());
                    dto.setCreatedAt(listing.getCreatedAt());
                    dto.setUpdatedAt(listing.getUpdatedAt());
                    dto.setAddress(listing.getAddress());
                    return dto;
                }).toList();
        BaseResponse<List<ListingReponseDTO>> response = new BaseResponse<>();
        if (listings.isEmpty()) {
            throw new CustomBusinessException("No listings found");
        }
        response.setData(listings);
        response.setStatus(200);
        response.setSuccess(true);
        response.setMessage("Get all listings");
        return response;
    }


    private Pageable buildPageable(int page, int size, String sort, String dir) {
        Sort s = (sort == null || sort.isBlank())
                ? Sort.by(Sort.Direction.DESC, "createdAt") //mặc định show acc mới tạo gần nhất
                : Sort.by("desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC, sort);
        return PageRequest.of(Math.max(page, 0), Math.max(size, 1), s); // số trang 0 âm, ít nhất 1 phần tử trong mỗi trang
    }

    @Override
    public BaseResponse<Map<String, Object>> searchCard(SearchListingRequestDTO requestDTO) {
        Pageable pageable = buildPageable(requestDTO.getPage(), requestDTO.getSize(), requestDTO.getSort(), requestDTO.getDir());

        Slice<SearchListingResponseDTO> lists = listingRepository.searchCards(requestDTO, pageable);

        if (lists.isEmpty()) throw new CustomBusinessException(ErrorCode.LISTING_NOT_FOUND.name());

        Map<String, Object> payload = Map.of(
                "items", lists.getContent(),
                "page", requestDTO.getPage(),
                "size", requestDTO.getSize(),
                "hasNext", lists.hasNext()
        );

        BaseResponse<Map<String, Object>> response = new BaseResponse<>();
        response.setData(payload);
        response.setStatus(200);
        response.setSuccess(true);
        response.setMessage("OK");

        return response;
    }

    @Override
    public BaseResponse<Map<String, Object>> getAllListForManage(int page, int size, String sort, String dir) {
        Pageable pageable = buildPageable(page, size, sort, dir);

        Slice<ListingListProjection> slice = listingRepository.getAllList(pageable);

        if (slice.isEmpty()) throw new CustomBusinessException(ErrorCode.LISTING_NOT_FOUND.name());

        Map<String, Object> payload = Map.of(
                "items", slice.getContent(),
                "page", page,
                "size", size,
                "hasNext", slice.hasNext()
        );

        BaseResponse<Map<String, Object>> response = new BaseResponse<>();
        response.setData(payload);
        response.setStatus(200);
        response.setSuccess(true);
        response.setMessage("OK");

        return response;
    }

    @Override
    public BaseResponse<Map<String, Object>> getSellerList(Long id, int page, int size, String sort, String dir) {
        Pageable pageable = buildPageable(page, size, sort, dir);

        if (id == null) throw new CustomBusinessException(ErrorCode.ACCOUNT_NOT_FOUND.name());

        Page<ListingListProjection> lists = listingRepository.findBySeller(id, pageable);
        if (lists.isEmpty()) throw new CustomBusinessException(ErrorCode.LISTING_NOT_FOUND.name());

        Map<String, Object> payload = Map.of(
                "items", lists.getContent(),
                "page", page,
                "size", size,
                "totalPages", lists.getTotalPages(),
                "totalElements", lists.getTotalElements(),
                "hasNext", lists.hasNext(),
                "hasPrevious", lists.hasPrevious()
        );

        BaseResponse<Map<String, Object>> response = new BaseResponse<>();
        response.setData(payload);
        response.setStatus(200);
        response.setSuccess(true);
        response.setMessage("OK");

        return response;
    }


//    @Override
//    public BaseResponse<ListingDetailResponseDTO> getListingById(long id) {
//        Optional<Listing> listing = listingRepository.findById(id);
//        if(listing.isPresent()) {
//            ListingDetailResponseDTO dto = new ListingDetailResponseDTO();
//
//        }
//
//    }

}
