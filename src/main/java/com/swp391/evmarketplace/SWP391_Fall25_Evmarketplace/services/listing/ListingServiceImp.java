package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.config.VNPayProperties;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.CreateListingRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.account.AccountReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.battery.BatteryListResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.branch.BranchDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.vehicle.VehicleListReponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ListingListProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.vnpay.VNPayService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.MedialUtils;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.PageableUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


@Service
public class ListingServiceImp implements ListingService {
    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private ListingMediaRepository listingMediaRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductBatteryRepository productBatteryRepository;
    @Autowired
    private ProductVehicleRepository productVehicleRepository;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private SalePaymentRepository salePaymentRepository;
    @Autowired
    private VNPayService  vNPayService;
    @Autowired
    private ListingMediaRepository mediaRepository;
    @Value("${server.url}")
    private String serverUrl;



    @Transactional
    @Override
    public BaseResponse<CreateListingResponse> createListing(CreateListingRequest req, List<MultipartFile> images, List<MultipartFile> videos) {
       try{
           // 1) Category
           var category = categoryRepository.findById(req.getCategoryId())
                   .orElseThrow(() -> new CustomBusinessException("Category not found"));

           // 2) Nếu có modelId → dùng model làm “nguồn chân lý” để nhận diện loại và kiểm tính hợp lệ
           Model model = null;
           if (req.getModelId() != null) {
               model = modelRepository.findById(req.getModelId())
                       .orElseThrow(() -> new CustomBusinessException("Model not found: " + req.getModelId()));

               // Model phải thuộc đúng category người dùng chọn
               if (!model.getCategory().getId().equals(req.getCategoryId())) {
                   throw new CustomBusinessException("Model does not belong to selected category");
               }
               // Nếu có brandId thì check model.brand
               if (req.getBrandId() != null && !model.getBrand().getId().equals(req.getBrandId())) {
                   throw new CustomBusinessException("Model does not belong to selected brand");
               }
           }

           // 3) Resolve loại (VEHICLE/BATTERY)
           ItemType type = resolveItemType(req, model);

           // Cross-check căn bản (tránh gửi nhầm code)
           if (type == ItemType.BATTERY && !"BATTERY".equalsIgnoreCase(req.getCategoryCode())) {
               throw new CustomBusinessException("categoryCode must be BATTERY for itemType=BATTERY");
           }
           if (type == ItemType.VEHICLE && "BATTERY".equalsIgnoreCase(req.getCategoryCode())) {
               throw new CustomBusinessException("categoryCode must not be BATTERY for itemType=VEHICLE");
           }

           // 4) Build listing snapshot
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
           listing.setStatus(req.getStatus());
           listing.setProvince(req.getProvince());
           listing.setDistrict(req.getDistrict());
           listing.setWard(req.getWard());
           listing.setAddress(req.getAddress());
           listing.setConsigned(false);

           // 5) Tự link catalog nếu có brandId + modelId
           if (req.getBrandId() != null && req.getModelId() != null) {
               if (type == ItemType.VEHICLE) {
                   productVehicleRepository
                           .findFirstByCategoryIdAndBrandIdAndModelId(req.getCategoryId(), req.getBrandId(), req.getModelId())
                           .ifPresent(listing::setProductVehicle);
                   // đảm bảo loại kia null
                   listing.setProductBattery(null);
               } else { // BATTERY
                   productBatteryRepository
                           .findFirstByCategory_IdAndBrand_IdAndModel_Id(req.getCategoryId(), req.getBrandId(), req.getModelId())
                           .ifPresent(listing::setProductBattery);
                   listing.setProductVehicle(null);
               }
           }

           listingRepository.save(listing);

           // 6) Media
           try {
               if (images != null) {
                   for (var img : images) {
                       if (img == null || img.isEmpty()) continue;
                       var stored = fileService.storeImage(img);
                       var media = new ListingMedia();
                       media.setListing(listing);
                       media.setMediaUrl(stored.getStoredName());
                       media.setMediaType(MediaType.IMAGE);
                       listing.addMedia(media);
                   }
               }
               if (videos != null) {
                   for (var v : videos) {
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

           var res = new BaseResponse<CreateListingResponse>();
           CreateListingResponse resp = new CreateListingResponse();
           resp.setListingId(listing.getId());
           resp.setPersistedStatus(listing.getStatus().name());
           res.setSuccess(true);
           res.setStatus(201);
           res.setData(resp);
           res.setMessage("Listing created");
           return res;
       } catch (Exception e) {
           throw new CustomBusinessException(e.getMessage());
       }
    }

    @Override
    public BaseResponse<Map<String, Object>> searchForPublic(SearchListingRequestDTO requestDTO, int page, int size, String sort, String dir) {
        return doSearch(requestDTO, EnumSet.of(ListingStatus.ACTIVE), page, size, sort, dir);
    }

    @Override
    public BaseResponse<Map<String, Object>> searchForManage(SearchListingRequestDTO requestDTO, int page, int size, String sort, String dir) {
        return doSearch(requestDTO, EnumSet.allOf(ListingStatus.class), page, size, sort, dir);
    }

    private ItemType resolveItemType(CreateListingRequest req, Model modelIfAny) {
        // Ưu tiên theo model.category nếu có modelId
        if (modelIfAny != null) {
            String cat = modelIfAny.getCategory().getName();
            return "BATTERY".equalsIgnoreCase(cat) ? ItemType.BATTERY : ItemType.VEHICLE;
        }
        // Rồi tới itemType client gửi
        if (req.getItemType() != null) return req.getItemType();
        // Sau cùng suy từ categoryCode
        return "BATTERY".equalsIgnoreCase(req.getCategoryCode()) ? ItemType.BATTERY : ItemType.VEHICLE;
    }


//    private Pageable buildPageable(int page, int size, String sort, String dir) {
//        Sort s = (sort == null || sort.isBlank())
//                ? Sort.by(Sort.Direction.DESC, "createdAt") //mặc định show acc mới tạo gần nhất
//                : Sort.by("desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC, sort);
//        return PageRequest.of(Math.max(page, 0), Math.max(size, 1), s); // số trang 0 âm, ít nhất 1 phần tử trong mỗi trang
//    }

    public BaseResponse<Map<String, Object>> doSearch(SearchListingRequestDTO requestDTO, EnumSet<ListingStatus> statusSet, int page, int size, String sort, String dir) {
        if (requestDTO.getKey() != null) {
            String key = requestDTO.getKey().trim();
            requestDTO.setKey(key.isEmpty() ? null : key);
        }
//        Pageable pageable = buildPageable(page, size, sort, dir);
        Pageable pageable = PageableUtils.buildPageable(page, size, sort, dir);
        Slice<ListingListProjection> lists = listingRepository.searchCards(requestDTO, statusSet, pageable);
        Map<String, Object> payload = Map.of(
                "items", lists.getContent(),
                "page", page,
                "size", size,
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
    public BaseResponse<Map<String, Object>> getAllListingsPublic(int page, int size, String sort, String dir) {
        Pageable pageable = PageableUtils.buildPageable(page, size, sort, dir);
        Slice<ListingListProjection> slice = listingRepository.getAllList(EnumSet.of(ListingStatus.ACTIVE), pageable);
        List<ListingListProjection> list = slice.getContent();
        for (ListingListProjection l : list){
            List<ListingMedia> mediaLists = listingMediaRepository.findAllByListingId(l.getId());
            if(mediaLists.size() > 0){

            }

        }

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
    public BaseResponse<Map<String, Object>> getAllListForManage(int page, int size, String sort, String dir) {
        Pageable pageable = PageableUtils.buildPageable(page, size, sort, dir);
        Slice<ListingListProjection> slice = listingRepository.getAllList(EnumSet.allOf(ListingStatus.class), pageable);

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
    public BaseResponse<PageResponse<ListingListItemDTO>> getMyListings(ListingStatus status, String q, Integer page, Integer size) {
        final int p = (page == null || page < 0) ? 0 : page;
        final int s = (size == null || size <= 0 || size > 100) ? 10 : size;

        Long sellerId = authUtil.getCurrentAccount().getId();

        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "updatedAt").and(Sort.by(Sort.Direction.DESC, "id")));

        Page<ListingListProjection> pg = listingRepository.findMine(sellerId, status, isBlank(q) ? null : q.trim(), pageable);

        List<ListingListItemDTO> items = pg.getContent().stream().map(prj ->
                ListingListItemDTO.builder()
                        .id(prj.getId())
                        .year(prj.getYear())
                        .status(prj.getStatus())
                        .visibility(prj.getVisibility())
                        .title(prj.getTitle())
                        .batteryCapacityKwh(prj.getBatteryCapacityKwh())
                        .createdAt(prj.getCreatedAt())
                        .province(prj.getProvince())
                        .mileageKm(prj.getMileageKm() == null ? null : String.valueOf(prj.getMileageKm()))
                        .sohPercent(prj.getSohPercent())
                        .model(prj.getModel())
                        .brand(prj.getBrand())
                        .price(prj.getPrice())
                        .isConsigned(prj.getIsConsigned())
                        .sellerName(prj.getSellerName())
                        .build()
        ).toList();

        PageResponse<ListingListItemDTO> body = PageResponse.<ListingListItemDTO>builder()
                .totalElements(pg.getTotalElements())
                .totalPages(pg.getTotalPages())
                .hasNext(pg.hasNext())
                .hasPrevious(pg.hasPrevious())
                .page(pg.getNumber())
                .size(pg.getSize())
                .items(items)
                .build();
        BaseResponse<PageResponse<ListingListItemDTO>> response = new BaseResponse<>();
        response.setData(body);
        response.setStatus(200);
        response.setSuccess(true);
        response.setMessage("OK");
        return response;
    }

    public Map<ListingStatus, Long> getMyCounts(Long sellerId) {
        Map<ListingStatus, Long> result = new EnumMap<>(ListingStatus.class);
        for (ListingStatus s : ListingStatus.values()) result.put(s, 0L); // fill 0

        var rows = listingRepository.countBySellerGroupedStatus(sellerId);
        for (var r : rows) result.put(r.getStatus(), r.getTotal());

        return result;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    //Payment Listing

    private long cfgLong(String key, long def) {
        return configRepository.findById(key)
                .map(c -> { try
                { return Long.parseLong(c.getValue());
                } catch (Exception e){ return def; }})
                .orElse(def);
    }

    @Override
    public BaseResponse<String> createPromotionPaymentUrl(Long listingId, HttpServletRequest request) {
        var me = authUtil.getCurrentAccount();
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        if (!listing.getSeller().getId().equals(me.getId()))
            throw new CustomBusinessException("Not your listing");

        if (listing.getStatus() != ListingStatus.APPROVED)
            throw new CustomBusinessException("Listing must be APPROVED to promote");

        long feeVnd = cfgLong("promoted_fee_vnd", 50000L);

        var pay = new SalePayment();
        pay.setListing(listing);
        pay.setPayer(me);
        pay.setAmount(BigDecimal.valueOf(feeVnd));
        pay.setMethod(PaymentMethod.VNPAY);
        pay.setPurpose(PaymentPurpose.PROMOTION);
        pay.setStatus(PaymentStatus.INIT);
        salePaymentRepository.save(pay);

        String txnRef = "PROMO-" + pay.getId();
        pay.setProviderTxnId(txnRef);
        salePaymentRepository.save(pay);

        Map<String,String> returnParams = Map.of(
                "purpose","PROMOTION",
                "listingId", String.valueOf(listingId),
                "paymentId", String.valueOf(pay.getId())
        );

        String paymentUrl = vNPayService.createPaymentUrl(
                feeVnd,
                "Thanh toan promote tin #" + listingId,
                VNPayProperties.getIpAddress(request),
                txnRef,
                returnParams,
                false
        );

        BaseResponse<String> res = new BaseResponse<>();
        res.setData(paymentUrl != null ? paymentUrl : "");
        res.setStatus(paymentUrl != null ? 200 : 500);
        res.setSuccess(paymentUrl != null);
        res.setMessage(paymentUrl != null ? "Get PaymentUrl Success" : "Get PaymentUrl Failed");
        return res;
    }


    //Lấy chi tiết bài đăng theo người bán
    @Override
    public BaseResponse<ListingDetailResponseDto> getListingDetailBySeller(Long listingId, Long sellerId) {
        Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new CustomBusinessException("Listing not found"));
        if(listing.getSeller().getId() != sellerId){
            throw new CustomBusinessException("Listing must be APPROVED to promote");
        }
        BaseResponse<ListingDetailResponseDto> res = new BaseResponse<>();
        ListingDetailResponseDto detail = convertToDto(listing);
        if(detail != null){
            res.setData(detail);
            res.setSuccess(true);
            res.setMessage("Get Listing Detail Success");
            res.setStatus(200);
        }
        return res;
    }

    //Lấy chi tiết bài đăng
    @Override
    public BaseResponse<ListingDetailResponseDto> getListingDetailById(Long listingId) {
        Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new CustomBusinessException("Listing not found"));
        BaseResponse<ListingDetailResponseDto> res = new BaseResponse<>();
        ListingDetailResponseDto detail = convertToDto(listing);
        if(detail != null){
            res.setData(detail);
            res.setSuccess(true);
            res.setMessage("Get Listing Detail Success");
            res.setStatus(200);
        }
        return res;
    }

    private ListingDetailResponseDto convertToDto(Listing listing) {
        ListingDetailResponseDto dto = new ListingDetailResponseDto();

        //Lấy Listing
        ListingDto listingDto = listing.toDto(listing, brandRepository, categoryRepository, modelRepository);
        dto.setListing(listingDto);

        //Lấy sellerId
        AccountReponseDTO accountDto = new AccountReponseDTO();
        accountDto.setId(listing.getSeller().getId());
        accountDto.setEmail(listing.getSeller().getEmail());
        accountDto.setProfile(listing.getSeller().getProfile());
        accountDto.setPhoneNumber(listing.getSeller().getPhoneNumber());
        dto.setSellerId(accountDto);

        //Lấy cơ sở giữ dùng cho kí gửi
        if(listing.getBranch() != null){
            BranchDto branchDto = listing.getBranch().toDto(listing.getBranch());
            dto.setBranch(branchDto);
        }

        //Lấy ProductVehicle nếu có
        if(listing.getProductVehicle() != null){
            VehicleListReponse vehicleDto = listing.getProductVehicle().toDto(listing.getProductVehicle());
            dto.setProductVehicle(vehicleDto);
        }

        //Lấy ProductBattery
        if(listing.getProductBattery() != null){
            BatteryListResponse batteryDto = listing.getProductBattery().toDto(listing.getProductBattery());
            dto.setProductBattery(batteryDto);
        }

        //Lấy các media của listing
        List<ListingMedia> mediaList = listingMediaRepository.findAllByListingId(listing.getId());
        if(mediaList.size() > 0){
            List<ListingMediaDto> dtos = new ArrayList<>();
            for(ListingMedia media : mediaList){
                ListingMediaDto listingMediaDto = media.toDto(media);
                listingMediaDto.setMediaUrl(MedialUtils.converMediaNametoMedialUrl(media.getMediaUrl(), media.getMediaType().name(), serverUrl));
                dtos.add(listingMediaDto);
            }
            dto.setMedia(dtos);
        }

        return dto;
    }




}
