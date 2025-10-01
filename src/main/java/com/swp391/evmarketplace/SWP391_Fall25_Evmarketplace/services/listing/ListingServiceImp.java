package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.CreateListingRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingListProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.SearchListingResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ErrorCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.MediaType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Status;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
public class ListingServiceImp implements ListingService {

    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private ListingMediaRepository listingMediaRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductBatteryRepository  productBatteryRepository;
    @Autowired
    private ProductVehicleRepository productVehicleRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private AuthUtil authUtil;

    @Value("${server.url}")
    private String serverUrl;


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
                    dto.setDistrict(listing.getDistrict());
                    dto.setWard(listing.getWard());
                    dto.setAddress(listing.getAddress());
                    dto.setPromotedUntil(listing.getPromotedUntil());

                    String thumbnail = "";
                    if( listing.getMediaList() != null && !listing.getMediaList().isEmpty()){
                        for(ListingMedia l : listing.getMediaList()){
                            if(l.getMediaType() == MediaType.IMAGE){
                                thumbnail = l.getMediaUrl();
                                break;
                            }
                        }
                    }
                    thumbnail = serverUrl + "/api/files/images/" + thumbnail;
                    if(!thumbnail.isEmpty()){
                        dto.setThumbnail(thumbnail);
                    }

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


    @Transactional
    @Override
    public BaseResponse<Void> createListing(CreateListingRequest req) {
        // 1) Validate & lookup
        var category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new CustomBusinessException("Category not found"));

        // (Tuỳ chọn) kiểm tra itemType ↔ categoryCode
        if (req.getItemType() == ItemType.BATTERY && !"BATTERY".equalsIgnoreCase(req.getCategoryCode())) {
            throw new CustomBusinessException("categoryCode must be BATTERY for itemType=BATTERY");
        }

        // 2) Build Listing snapshot
        var listing = new Listing();
        listing.setCategory(category);
        listing.setTitle(req.getTitle());
        listing.setSeller(authUtil.getCurrentAccount());

        listing.setBrand(req.getBrand());
        listing.setBrandId(req.getBrandId());
        listing.setModel(req.getModel());
        listing.setModelId(req.getModelId());
        listing.setYear(req.getYear());
        listing.setBatteryCapacityKwh(req.getBatteryCapacityKwh());
        listing.setSohPercent(req.getSohPercent());
        listing.setMileageKm(req.getMileageKm());
        listing.setColor(req.getColor());
        listing.setDescription(req.getDescription());
        listing.setPrice(req.getPrice());
        listing.setVisibility(req.getVisibility());
        listing.setVerified(false);
        listing.setStatus(Status.PENDING);
        listing.setProvince(req.getProvince());
        listing.setDistrict(req.getDistrict());
        listing.setWard(req.getWard());
        listing.setAddress(req.getAddress());
        listing.setConsigned(false);

        if (req.getItemType() == ItemType.VEHICLE && req.getBrandId() != null && req.getModelId() != null) {
            productVehicleRepository
                    .findFirstByCategoryIdAndBrandIdAndModelId(req.getCategoryId(), req.getBrandId(), req.getModelId())
                    .ifPresent(listing::setProductVehicle);
        } else if (req.getItemType() == ItemType.BATTERY && req.getBrandId() != null && req.getModelId() != null) {
            productBatteryRepository
                    .findFirstByCategoryIdAndBrandIdAndModelId(req.getCategoryId(), req.getBrandId(), req.getModelId())
                    .ifPresent(listing::setProductBattery);
        }

        listingRepository.save(listing);


        try {
            if (req.getImages() != null) {
                for (var img : req.getImages()) {
                    if (img == null || img.isEmpty()) continue;
                    var stored = fileService.storeImage(img);
                    var media = new ListingMedia();
                    media.setListing(listing);
                    media.setMediaUrl(stored.getStoredName());
                    media.setMediaType(MediaType.IMAGE);
                    listing.addMedia(media);
                }
            }
            if (req.getVideos() != null) {
                for (var v : req.getVideos()) {
                    if (v == null || v.isEmpty()) continue;
                    var stored = fileService.storeVideo(v);
                    var media = new ListingMedia();
                    media.setListing(listing);
                    media.setMediaUrl(stored.getStoredName());
                    media.setMediaType(MediaType.VIDEO);
                    listing.addMedia(media);
                }
            }
        } catch (IOException ioe) {
            throw new CustomBusinessException("Upload media failed: " + ioe.getMessage());
        }


        listingRepository.save(listing);

        var res = new BaseResponse<Void>();
        res.setSuccess(true);
        res.setStatus(201);
        res.setMessage("Listing created");
        return res;
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




}
