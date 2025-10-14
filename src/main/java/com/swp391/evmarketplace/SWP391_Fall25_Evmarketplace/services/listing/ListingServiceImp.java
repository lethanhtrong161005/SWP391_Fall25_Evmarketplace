package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.CreateListingRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.SearchListingRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing.UpdateListingRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.account.AccountReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.battery.BatteryListResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.branch.BranchDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.vehicle.VehicleListReponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.listing.ListingCardDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
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
    private VNPayService vNPayService;
    @Autowired
    private ListingMediaRepository mediaRepository;
    @Value("${server.url}")
    private String serverUrl;


    @Transactional
    @Override
    public BaseResponse<CreateListingResponse> createListing(CreateListingRequest req, List<MultipartFile> images, List<MultipartFile> videos) {
        try {
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
                    listing.setVoltage(req.getVoltageV());
                    listing.setBatteryChemistry(req.getBatteryChemistry());
                    listing.setMassKg(req.getMassKg());
                    listing.setDimensions(req.getDimensionsMm());

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


    private Pageable buildPageable(int page, int size, String sort, String dir) {
        Sort s = (sort == null || sort.isBlank())
                ? Sort.by(Sort.Direction.DESC, "createdAt") //mặc định show acc mới tạo gần nhất
                : Sort.by("desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC, sort);
        return PageRequest.of(Math.max(page, 0), Math.max(size, 1), s); // số trang 0 âm, ít nhất 1 phần tử trong mỗi trang
    }

    public BaseResponse<Map<String, Object>> doSearch(SearchListingRequestDTO requestDTO, EnumSet<ListingStatus> statusSet, int page, int size, String sort, String dir) {
        if (requestDTO.getKey() != null) {
            String key = requestDTO.getKey().trim();
            requestDTO.setKey(key.isEmpty() ? null : key);
        }
        Pageable pageable = buildPageable(page, size, sort, dir);
        Slice<ListingListProjection> slice = listingRepository.searchCards(requestDTO, statusSet, pageable);
        if (slice.isEmpty()) throw new CustomBusinessException(ErrorCode.LISTING_NOT_FOUND.name());

        List<ListingCardDTO> items = slice.getContent().stream()
                .map(this::toCardDtoWithFav)
                .toList();

        Map<String, Object> payload = Map.of(
                "items", items,
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
    public BaseResponse<Map<String, Object>> getAllListingsPublic(int page, int size, String sort, String dir) {
        Pageable pageable = buildPageable(page, size, sort, dir);

        Long userId = authUtil.getCurrentAccountIdOrNull();

        Slice<ListingListProjection> slice = listingRepository.getAllListWithFavPublic(EnumSet.of(ListingStatus.ACTIVE), userId, pageable);

        if (slice.isEmpty()) throw new CustomBusinessException(ErrorCode.LISTING_NOT_FOUND.name());

        List<ListingCardDTO> items = slice.getContent().stream()
                .map(this::toCardDtoWithFav)
                .toList();

        Map<String, Object> payload = Map.of(
                "items", items,
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
        Pageable pageable = buildPageable(page, size, sort, dir);

        Long userId = authUtil.getCurrentAccountIdOrNull();

        Page<ListingListProjection> pages = listingRepository.getAllListWithFavManage(EnumSet.allOf(ListingStatus.class), userId, pageable);

        if (pages.isEmpty()) throw new CustomBusinessException(ErrorCode.LISTING_NOT_FOUND.name());

        List<ListingCardDTO> items = pages.getContent().stream()
                .map(this::toCardDtoWithFav)
                .toList();

        Map<String, Object> payload = Map.of(
                "items", items,
                "page", page,
                "size", size,
                "totalPages", pages.getTotalPages(),
                "totalElements", pages.getTotalElements(),
                "hasNext", pages.hasNext(),
                "hasPrevious", pages.hasPrevious()
        );

        BaseResponse<Map<String, Object>> response = new BaseResponse<>();
        response.setData(payload);
        response.setStatus(200);
        response.setSuccess(true);
        response.setMessage("OK");

        return response;
    }

    private ListingCardDTO toCardDto(ListingListProjection prj) {

        String thumbName = listingMediaRepository.findThumbnailUrlByListingId(prj.getId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.NOT_FOUND_IMAGE.name()));

        String thumbUrl = (thumbName == null || thumbName.isBlank())
                ? null
                : MedialUtils.converMediaNametoMedialUrl(thumbName, MediaType.IMAGE.name(), serverUrl);

        return ListingCardDTO.builder()
                .id(prj.getId())
                .categoryId(prj.getCategoryId())
                .title(prj.getTitle())
                .brand(prj.getBrand())
                .model(prj.getModel())
                .year(prj.getYear())
                .sellerName(prj.getSellerName())
                .price(prj.getPrice())
                .province(prj.getProvince())
                .batteryCapacityKwh(prj.getBatteryCapacityKwh())
                .sohPercent(prj.getSohPercent())
                .mileageKm(prj.getMileageKm())
                .createdAt(prj.getCreatedAt())
                .status(prj.getStatus())
                .visibility(prj.getVisibility())
                .isConsigned(prj.getIsConsigned())
                .thumbnailUrl(thumbUrl)
                .build();
    }

    private ListingCardDTO toCardDtoWithFav(ListingListProjection prj) {
        ListingCardDTO dto = toCardDto(prj);
        dto.setFavoriteCount(prj.getFavoriteCount());
        dto.setLikedByCurrentUser(prj.getLikedByCurrentUser());
        return dto;
    }

    @Override
    public BaseResponse<PageResponse<ListingListItemDTO>> getMyListings(
            ListingStatus status, String q, Integer page, Integer size) {

        final int p = (page == null || page < 0) ? 0 : page;
        final int s = (size == null || size <= 0 || size > 100) ? 10 : size;

        Long sellerId = authUtil.getCurrentAccount().getId();

        Sort sort = (status == ListingStatus.SOFT_DELETED)
                ? Sort.by(Sort.Direction.DESC, "deletedAt")
                .and(Sort.by(Sort.Direction.DESC, "updatedAt"))
                .and(Sort.by(Sort.Direction.DESC, "id"))
                : Sort.by(Sort.Direction.DESC, "updatedAt")
                .and(Sort.by(Sort.Direction.DESC, "id"));

        Pageable pageable = PageRequest.of(p, s, sort);

        Page<ListingListProjection> pg = listingRepository.findMine(
                sellerId,
                status,
                (q == null || q.isBlank()) ? null : q.trim(),
                pageable
        );

        List<ListingListItemDTO> items = pg.getContent().stream().map(prj -> {
            LocalDateTime deletedAt = prj.getDeletedAt();
            LocalDateTime purgeAt = (deletedAt != null) ? deletedAt.plusDays(30) : null;

            return ListingListItemDTO.builder()
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
                    .updatedAt(prj.getUpdatedAt())
                    .deletedAt(deletedAt)
                    .expiresAt(prj.getExpiresAt())
                    .promotedUntil(prj.getPromotedUntil())
                    .hiddenAt(prj.getHiddenAt())
                    .purgeAt(purgeAt)
                    .build();
        }).toList();

        PageResponse<ListingListItemDTO> body = PageResponse.<ListingListItemDTO>builder()
                .totalElements(pg.getTotalElements())
                .totalPages(pg.getTotalPages())
                .hasNext(pg.hasNext())
                .hasPrevious(pg.hasPrevious())
                .page(pg.getNumber())
                .size(pg.getSize())
                .items(items)
                .build();

        BaseResponse<PageResponse<ListingListItemDTO>> res = new BaseResponse<>();
        res.setData(body);
        res.setStatus(200);
        res.setSuccess(true);
        res.setMessage("OK");
        return res;
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
                .map(c -> {
                    try {
                        return Long.parseLong(c.getValue());
                    } catch (Exception e) {
                        return def;
                    }
                })
                .orElse(def);
    }

    //Lấy chi tiết bài đăng theo người bán
    @Override
    public BaseResponse<ListingDetailResponseDto> getListingDetailBySeller(Long listingId, Long sellerId) {
        Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new CustomBusinessException("Listing not found"));
        if (listing.getSeller().getId() != sellerId) {
            throw new CustomBusinessException("Listing must be APPROVED to promote");
        }
        BaseResponse<ListingDetailResponseDto> res = new BaseResponse<>();
        ListingDetailResponseDto detail = convertToDto(listing);
        if (detail != null) {
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
        if (detail != null) {
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
        if (listing.getBranch() != null) {
            BranchDto branchDto = listing.getBranch().toDto(listing.getBranch());
            dto.setBranch(branchDto);
        }

        //Lấy ProductVehicle nếu có
        if (listing.getProductVehicle() != null) {
            VehicleListReponse vehicleDto = listing.getProductVehicle().toDto(listing.getProductVehicle());
            dto.setProductVehicle(vehicleDto);
        }

        //Lấy ProductBattery
        if (listing.getProductBattery() != null) {
            BatteryListResponse batteryDto = listing.getProductBattery().toDto(listing.getProductBattery());
            dto.setProductBattery(batteryDto);
        }

        //Lấy các media của listing
        List<ListingMedia> mediaList = listingMediaRepository.findAllByListingId(listing.getId());
        if (mediaList.size() > 0) {
            List<ListingMediaDto> dtos = new ArrayList<>();
            for (ListingMedia media : mediaList) {
                ListingMediaDto listingMediaDto = media.toDto(media);
                listingMediaDto.setMediaUrl(MedialUtils.converMediaNametoMedialUrl(media.getMediaUrl(), media.getMediaType().name(), serverUrl));
                dtos.add(listingMediaDto);
            }
            dto.setMedia(dtos);
        }

        return dto;
    }

    @Transactional
    @Override
    public BaseResponse<?> updatedListing(
            Long id,
            Long sellerId,
            UpdateListingRequest req,
            List<MultipartFile> images,
            List<MultipartFile> videos,
            List<Long> keepMediaIds
    ) {

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new CustomBusinessException("Listing not found"));


        if (!Objects.equals(listing.getSeller().getId(), sellerId)) {
            throw new CustomBusinessException("You are not the owner of this listing");
        }

        // Danh sách trạng thái cho role MEMBER
        EnumSet<ListingStatus> EDITABLE_BY_MEMBER = EnumSet.of(
                ListingStatus.PENDING,
                ListingStatus.REJECTED,
                ListingStatus.EXPIRED
        );

        if (listing.getSeller().getRole() == AccountRole.MEMBER) {
            // Chỉ các trạng thái cho phép
            if (!EDITABLE_BY_MEMBER.contains(listing.getStatus())) {
                throw new CustomBusinessException("Listings can not be edited");
            }

            // 2) Nếu đang EXPIRED → áp dụng cooldown 7 ngày khi gia hạn NORMAL
            if (listing.getStatus() == ListingStatus.EXPIRED) {
                final int RENEW_COOLDOWN_DAYS = 7;

                // Lấy visibility target (nếu FE không gửi thì giữ nguyên)
                Visibility targetVis = (req.getVisibility() != null)
                        ? req.getVisibility()
                        : listing.getVisibility();

                // Cho nâng lên BOOSTED ngay, chặn chỉ khi target là NORMAL và chưa hết cooldown
                if (targetVis == Visibility.NORMAL) {
                    // Ưu tiên dùng expiredAt, nếu chưa có thì fallback updatedAt
                    LocalDateTime expiredAt =
                            listing.getExpiresAt() != null ? listing.getExpiresAt() : listing.getUpdatedAt();

                    if (expiredAt == null ||
                            LocalDateTime.now().isBefore(expiredAt.plusDays(RENEW_COOLDOWN_DAYS))) {
                        throw new CustomBusinessException(
                                "You can only renew the regular mode 7 days after it expires."
                        );
                    }
                }
                // targetVis == BOOSTED → pass
            }
        }

        // 2) Không cho đổi category
        final Long categoryId = listing.getCategory().getId();
        final boolean isBatteryCategory =
                "BATTERY".equalsIgnoreCase(
                        listing.getCategory().getName());

        // ===== BRAND / MODEL — hỗ trợ 2 chế độ =====
        // A. BRAND
        if (req.getBrandId() != null) {
            // chuyển sang (hoặc giữ) chế độ catalog theo brandId
            if (!Objects.equals(listing.getBrandId(), req.getBrandId())) {
                Brand brand = brandRepository.findById(req.getBrandId())
                        .orElseThrow(() -> new CustomBusinessException("Brand not found: " + req.getBrandId()));
                listing.setBrandId(brand.getId());
                listing.setBrand(
                        (req.getBrand() != null && !req.getBrand().isBlank()) ? req.getBrand() : brand.getName()
                );

                listing.setModelId(null);
                listing.setModel(null);
                listing.setProductVehicle(null);
                listing.setProductBattery(null);
            } else if (req.getBrand() != null && !req.getBrand().isBlank()) {
                listing.setBrand(req.getBrand());
            }
        } else {
            // FE không gửi brandId → có thể là chế độ "ngoài catalog"
            if (req.getBrand() != null && !req.getBrand().isBlank()) {
                listing.setBrand(req.getBrand());
            }
            // nếu client muốn chuyển từ catalog → ngoài catalog (brandId=null)
            if (req.getBrand() == null && req.getBrandId() != null) {
                // (trường hợp này không xảy ra vì đã nằm ở nhánh trên)
            } else if (req.getBrandId() == null && req.getBrand() != null) {
                // brandId null + brand text có → tắt catalog brand
                if (listing.getBrandId() != null) {
                    listing.setBrandId(null);
                    // reset liên quan model/product vì đã rời catalog
                    listing.setModelId(null);
                    listing.setProductVehicle(null);
                    listing.setProductBattery(null);
                }
            }
        }

        // B. MODEL
        if (req.getModelId() != null) {
            // validate model
            Model model = modelRepository.findById(req.getModelId())
                    .orElseThrow(() -> new CustomBusinessException("Model not found: " + req.getModelId()));

            if (!Objects.equals(model.getCategory().getId(), categoryId)) {
                throw new CustomBusinessException("Model does not belong to listing category");
            }
            // Nếu có brandId từ req, check model belongs brand đó
            Long effectiveBrandId = (req.getBrandId() != null ? req.getBrandId() : listing.getBrandId());
            if (effectiveBrandId != null && !Objects.equals(model.getBrand().getId(), effectiveBrandId)) {
                throw new CustomBusinessException("Model does not belong to selected/current brand");
            }

            listing.setModelId(model.getId());
            listing.setModel((req.getModel() != null && !req.getModel().isBlank()) ? req.getModel() : model.getName());

            // Relink product nếu đủ brandId + modelId (catalog mode)
            if (listing.getBrandId() != null && listing.getModelId() != null) {
                if (isBatteryCategory) {
                    productBatteryRepository
                            .findFirstByCategory_IdAndBrand_IdAndModel_Id(categoryId, listing.getBrandId(), listing.getModelId())
                            .ifPresent(listing::setProductBattery);
                    listing.setProductVehicle(null);
                } else {
                    productVehicleRepository
                            .findFirstByCategoryIdAndBrandIdAndModelId(categoryId, listing.getBrandId(), listing.getModelId())
                            .ifPresent(listing::setProductVehicle);
                    listing.setProductBattery(null);
                }
            } else {
                // thiếu id → không link catalog
                listing.setProductVehicle(null);
                listing.setProductBattery(null);
            }
        } else {
            // không có modelId → có thể là ngoài catalog
            if (req.getModel() != null && !req.getModel().isBlank()) {
                listing.setModel(req.getModel());
            }
            // Nếu brandId/modelId không đủ → unlink product
            if (req.getModelId() == null && (req.getBrandId() == null || listing.getBrandId() == null)) {
                listing.setModelId(null);
                listing.setProductVehicle(null);
                listing.setProductBattery(null);
            }
        }

        // ===== FIELDS CHUNG =====
        if (notBlank(req.getTitle())) listing.setTitle(req.getTitle());
        if (req.getYear() != null) listing.setYear(req.getYear());
        if (notBlank(req.getColor())) listing.setColor(req.getColor());

        if (req.getBatteryCapacityKwh() != null) listing.setBatteryCapacityKwh(req.getBatteryCapacityKwh());
        if (req.getSohPercent() != null) listing.setSohPercent(req.getSohPercent());
        if (req.getMileageKm() != null) listing.setMileageKm(req.getMileageKm());

        if (req.getPrice() != null) listing.setPrice(req.getPrice());
        if (notBlank(req.getDescription())) listing.setDescription(req.getDescription());

        if (notBlank(req.getProvince())) listing.setProvince(req.getProvince());
        if (notBlank(req.getDistrict())) listing.setDistrict(req.getDistrict());
        if (notBlank(req.getWard())) listing.setWard(req.getWard());
        if (notBlank(req.getAddress())) listing.setAddress(req.getAddress());

        if (req.getVisibility() != null) listing.setVisibility(req.getVisibility());
        if (req.getStatus() != null) listing.setStatus(req.getStatus());
        if (ListingStatus.REJECTED == req.getStatus()) {
            listing.setStatus(ListingStatus.PENDING);
        } else if (ListingStatus.EXPIRED == req.getStatus()) {
            listing.setStatus(ListingStatus.PENDING);
        }

        // PIN (nếu category là BATTERY)
        if (isBatteryCategory) {
            if (req.getVoltageV() != null) listing.setVoltage(req.getVoltageV());
            if (req.getBatteryChemistry() != null) listing.setBatteryChemistry(req.getBatteryChemistry());
            if (req.getMassKg() != null) listing.setMassKg(req.getMassKg());
            if (req.getDimensionsMm() != null) listing.setDimensions(req.getDimensionsMm());
        }

        // ===== MEDIA =====
        // Nếu keepMediaIds == null → hiểu là "giữ nguyên tất cả" (chỉ thêm file mới).
        System.out.println("MediaId: " + keepMediaIds.size());
        try {
            if (keepMediaIds != null) {
                List<ListingMedia> current = listingMediaRepository.findAllByListingId(listing.getId());
                for (ListingMedia m : current) {
                    if (!keepMediaIds.contains(m.getId())) {
                        System.out.println(m.getId() + " - " + m.getMediaUrl());
                        if (m.getMediaType() == MediaType.IMAGE) {
                            fileService.deleteImage(m.getMediaUrl());
                        } else if (m.getMediaType() == MediaType.VIDEO) {
                            fileService.deleteVideo(m.getMediaUrl());
                        }
                        listingMediaRepository.delete(m);
                    }
                }
            }
        } catch (Exception e) {
            throw new CustomBusinessException("Failed to delete listing media");
        }

        try {
            if (images != null) {
                for (MultipartFile img : images) {
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
                for (MultipartFile v : videos) {
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

        BaseResponse<?> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("Listing updated");
        return res;
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    @Transactional
    @Override
    public BaseResponse<?> deleteListing(Long listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new CustomBusinessException("Listing not found"));

        var actor = authUtil.getCurrentAccount();
        var role = actor.getRole();
        boolean isOwner = listing.getSeller().getId().equals(actor.getId());
        boolean isConsign = Boolean.TRUE.equals(listing.getConsigned());
        ListingStatus st = listing.getStatus();

        // 1) Consigned: chỉ cho xoá khi SOLD (đã bán xong)
        if (isConsign && st != ListingStatus.SOLD) {
            throw new CustomBusinessException("Consigned listing can only be deleted when SOLD");
        }

        // 2) Phân quyền theo trạng thái
        switch (role) {
            case STAFF -> throw new CustomBusinessException("You are not allowed to delete this listing");

            case MEMBER -> {
                // Member chỉ xoá mềm tin của chính mình và khi PENDING/REJECTED
                boolean canDelete = isOwner && (st == ListingStatus.PENDING || st == ListingStatus.REJECTED
                        || st == ListingStatus.EXPIRED || st == ListingStatus.HIDDEN || st == ListingStatus.APPROVED);
                if (!canDelete) {
                    throw new CustomBusinessException("You can only delete your own PENDING/REJECTED/HIDDEN/EXPIRED/APPROVED listing");
                }
            }

            case MANAGER, ADMIN -> {
                // Tuỳ yêu cầu có thể chặn thêm RESERVED (đang giữ chỗ)
                if (st == ListingStatus.RESERVED) {
                    throw new CustomBusinessException("Reserved listing cannot be deleted");
                }
                // SOLD consigned: nếu cần, có thể kiểm tra đã đối soát xong mới cho xoá
                // if (isConsign && st == ListingStatus.SOLD && !settlementService.isPayoutDone(listing)) {
                //     throw new CustomBusinessException("Settlement not paid yet");
                // }
            }

            default -> throw new CustomBusinessException("You are not allowed to delete this listing");
        }

        // 3) Lưu previous state để hỗ trợ khôi phục sau này
        listing.setPrevStatus(listing.getStatus());
        listing.setPrevVisibility(listing.getVisibility());
        listing.setPrevExpiresAt(listing.getExpiresAt());

        // 4) Ghi history (audit trail)
        ListingStatusHistory h = new ListingStatusHistory();
        h.setListing(listing);
        h.setFromStatus(listing.getPrevStatus());
        h.setToStatus(ListingStatus.SOFT_DELETED);
        h.setActor(actor);
        h.setReason("SOFT_DELETE");
        h.setNote("User requested delete");
        listing.addHistory(h);

        // 5) Chuyển trạng thái sang SOFT_DELETED + đánh dấu thời điểm
        listing.setStatus(ListingStatus.SOFT_DELETED);
        listing.setDeletedAt(LocalDateTime.now());
        if (listing.getHiddenAt() == null) {
            listing.setHiddenAt(LocalDateTime.now());
        }

        listingRepository.save(listing);

        BaseResponse<?> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("Listing updated");
        return res;
    }

    @Transactional
    @Override
    public BaseResponse<?> changeStatus(Long listingId, ListingStatus target) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new CustomBusinessException("Listing not found"));

        var actor = authUtil.getCurrentAccount();
        boolean isOwner = listing.getSeller() != null && listing.getSeller().getId().equals(actor.getId());

        // 0) Chỉ MEMBER đổi trạng thái bài của mình
        if (actor.getRole() != AccountRole.MEMBER || !isOwner) {
            throw new CustomBusinessException("You are not allowed to change this listing status");
        }

        // 1) MEMBER không thao tác bài ký gửi
        if (Boolean.TRUE.equals(listing.getConsigned())) {
            throw new CustomBusinessException("Consigned listing cannot be changed by member");
        }

        final ListingStatus current = listing.getStatus();
        final Visibility vis = listing.getVisibility();
        final LocalDateTime now = LocalDateTime.now();

        // hạn hiện tại (tuỳ visibility)
        LocalDateTime currentDeadline =
                (vis == Visibility.BOOSTED) ? listing.getPromotedUntil() : listing.getExpiresAt();

        // log history helper
        Runnable logHistory = () -> {
            ListingStatusHistory h = new ListingStatusHistory();
            h.setListing(listing);
            h.setFromStatus(current);
            h.setToStatus(listing.getStatus());
            h.setActor(actor);
            h.setReason("STATUS_CHANGE");
            listing.addHistory(h);
        };

        switch (current) {
            /* ====================== ACTIVE → ... ====================== */
            case ACTIVE -> {
                if (target != ListingStatus.HIDDEN) {
                    throw new CustomBusinessException("Only allow ACTIVE → HIDDEN");
                }
                listing.setPrevStatus(current);
                listing.setPrevVisibility(vis);
                listing.setPrevExpiresAt(currentDeadline);

                listing.setStatus(ListingStatus.HIDDEN);
            }

            /* ====================== HIDDEN → ... ====================== */
            case HIDDEN -> {
                if (target == ListingStatus.SOFT_DELETED) {
                    listing.setStatus(ListingStatus.SOFT_DELETED);
                    listing.setDeletedAt(now);
                    listing.setHiddenAt(null);
                } else if (target == ListingStatus.ACTIVE) {
                    Visibility prevVis = listing.getPrevVisibility();
                    LocalDateTime prevDeadline = listing.getPrevExpiresAt();

                    if (listing.getPrevStatus() != ListingStatus.ACTIVE || prevVis == null) {
                        throw new CustomBusinessException("Cannot restore: missing previous active state");
                    }

                    boolean stillValid = prevDeadline != null && prevDeadline.isAfter(now);
                    if (stillValid) {
                        listing.setStatus(ListingStatus.ACTIVE);
                        listing.setVisibility(prevVis);
                        if (prevVis == Visibility.BOOSTED) listing.setPromotedUntil(prevDeadline);
                        else listing.setExpiresAt(prevDeadline);
                    } else {
                        listing.setStatus(ListingStatus.EXPIRED);
                    }
                } else {
                    throw new CustomBusinessException("Only allow HIDDEN → SOFT_DELETED/ACTIVE");
                }
            }

            /* ====================== SOFT_DELETED → ... ====================== */
            case SOFT_DELETED -> {
                if (target != ListingStatus.ACTIVE) {
                    throw new CustomBusinessException("Only allow SOFT_DELETED → ACTIVE (restore)");
                }

                Visibility prevVis = listing.getPrevVisibility();
                LocalDateTime prevDeadline = listing.getPrevExpiresAt();

                if (listing.getPrevStatus() != ListingStatus.ACTIVE || prevVis == null) {
                    throw new CustomBusinessException("Cannot restore: missing previous active state");
                }

                boolean stillValid = prevDeadline != null && prevDeadline.isAfter(now);
                if (stillValid) {
                    listing.setStatus(ListingStatus.ACTIVE);
                    listing.setVisibility(prevVis);
                    if (prevVis == Visibility.BOOSTED) listing.setPromotedUntil(prevDeadline);
                    else listing.setExpiresAt(prevDeadline);
                } else {
                    listing.setStatus(ListingStatus.EXPIRED);
                }


            }

            /* ====== PENDING / REJECTED / EXPIRED / APPROVED → SOFT_DELETED ====== */
            case PENDING, REJECTED, EXPIRED, APPROVED -> {
                if (target != ListingStatus.SOFT_DELETED) {
                    throw new CustomBusinessException("Only allow PENDING/REJECTED/EXPIRED/APPROVED → SOFT_DELETED");
                }
                // Lưu prev* (để nếu sau này có cơ chế khôi phục thì vẫn có dấu vết)
                listing.setPrevStatus(current);
                listing.setPrevVisibility(vis);
                listing.setPrevExpiresAt(currentDeadline);

                listing.setStatus(ListingStatus.SOFT_DELETED);
            }

            /* ====================== Các trạng thái khác ====================== */
            case RESERVED, SOLD -> {
                throw new CustomBusinessException("You are not allowed to change status from " + current);
            }

            default -> throw new CustomBusinessException("Unsupported current status: " + current);
        }

        listing.setUpdatedAt(now);
        listingRepository.save(listing);
        logHistory.run();

        BaseResponse<?> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("Listing status updated");
        return res;
    }

    @Transactional
    @Override
    public BaseResponse<?> restore(Long listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new CustomBusinessException("Listing not found"));

        var actor = authUtil.getCurrentAccount();
        boolean isOwner = listing.getSeller() != null && listing.getSeller().getId().equals(actor.getId());

        // Chỉ MEMBER khôi phục bài của mình
        if (actor.getRole() != AccountRole.MEMBER || !isOwner) {
            throw new CustomBusinessException("You are not allowed to restore this listing");
        }
        // MEMBER không thao tác bài ký gửi
        if (Boolean.TRUE.equals(listing.getConsigned())) {
            throw new CustomBusinessException("Consigned listing cannot be restored by member");
        }

        final ListingStatus current = listing.getStatus();
        final LocalDateTime now = LocalDateTime.now();

        if (current != ListingStatus.HIDDEN && current != ListingStatus.SOFT_DELETED) {
            throw new CustomBusinessException("Only HIDDEN or SOFT_DELETED can be restored");
        }

        ListingStatus prevStatus = listing.getPrevStatus();
        Visibility prevVis = listing.getPrevVisibility();
        LocalDateTime prevDeadline = listing.getPrevExpiresAt();

        if (prevStatus == null) {
            throw new CustomBusinessException("Cannot restore: missing previous state");
        }

        ListingStatus toStatus;
        Visibility toVis = listing.getVisibility();

        switch (prevStatus) {
            case ACTIVE -> {
                boolean stillValid = prevDeadline != null && prevDeadline.isAfter(now);
                if (stillValid) {
                    toStatus = ListingStatus.ACTIVE;
                    toVis = (prevVis != null ? prevVis : listing.getVisibility());
                    if (toVis == Visibility.BOOSTED) {
                        listing.setPromotedUntil(prevDeadline);
                        listing.setExpiresAt(null);
                    } else {
                        listing.setExpiresAt(prevDeadline);
                        listing.setPromotedUntil(null);
                    }
                } else {
                    toStatus = ListingStatus.EXPIRED;
                }
            }
            case PENDING, REJECTED, APPROVED, EXPIRED -> {
                toStatus = prevStatus;
            }
            default -> throw new CustomBusinessException("Previous state is not restorable: " + prevStatus);
        }

        ListingStatus fromStatus = listing.getStatus();
        listing.setStatus(toStatus);
        listing.setVisibility(toVis);
        listing.setHiddenAt(null);
        listing.setDeletedAt(null);
        listing.setPrevStatus(null);
        listing.setPrevVisibility(null);
        listing.setPrevExpiresAt(null);
        listing.setUpdatedAt(now);

        listingRepository.save(listing);

        ListingStatusHistory h = new ListingStatusHistory();
        h.setListing(listing);
        h.setFromStatus(fromStatus);
        h.setToStatus(toStatus);
        h.setActor(actor);
        h.setReason("RESTORE");
        listing.addHistory(h);

        BaseResponse<?> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("Listing restored from " + fromStatus + " to " + toStatus);
        return res;
    }


}
