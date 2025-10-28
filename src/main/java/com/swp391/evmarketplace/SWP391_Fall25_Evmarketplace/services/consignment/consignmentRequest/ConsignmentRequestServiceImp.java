package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentRequest;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.request.ConsignmentRequestListItemDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentRequestProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.MedialUtils;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentRequestStatus.*;

@Service
public class ConsignmentRequestServiceImp implements ConsignmentRequestService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private ConsignmentRequestRepository consignmentRequestRepository;
    @Autowired
    private CategoryBrandRepository categoryBrandRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private ConsignmentRequestMediaRepository consignmentRequestMediaRepository;
    @Autowired
    AuthUtil authUtil;
    @Value("${server.url}")
    private String serverUrl;
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    @Override
    public BaseResponse<Void> createConsignmentRequest(CreateConsignmentRequestDTO requestDTO, List<MultipartFile> images, List<MultipartFile> videos) {
        Account account = authUtil.getCurrentAccount();

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CATEGORY_NOT_FOUND.name()));
        Branch branch = branchRepository.findById(requestDTO.getPreferredBranchId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.BRANCH_NOT_FOUND.name()));

        if (branch.getStatus() != BranchStatus.ACTIVE)
            throw new CustomBusinessException(ErrorCode.BRANCH_INACTIVE.name());

        if (category.getStatus() != CategoryStatus.ACTIVE)
            throw new CustomBusinessException(ErrorCode.CATEGORY_INACTIVE.name());

        String brandName = requestDTO.getBrand();
        String modelName = requestDTO.getModel();
        Integer year = requestDTO.getYear();

        Model model = null;
        if (requestDTO.getModelId() != null) {
            model = modelRepository.findById(requestDTO.getModelId())
                    .orElseThrow(() -> new CustomBusinessException(ErrorCode.MODEL_NOT_FOUND.name() + ": " + requestDTO.getModelId()));

            if (requestDTO.getCategoryId() != null) {
                if (!model.getCategory().getId().equals(requestDTO.getCategoryId())) {
                    throw new CustomBusinessException(ErrorCode.MODEL_NOT_BELONG_TO_CATEGORY.name());
                }
            }

            if (requestDTO.getBrandId() != null) {
                if (!model.getBrand().getId().equals(requestDTO.getBrandId())) {
                    throw new CustomBusinessException(ErrorCode.MODEL_NOT_BELONG_TO_BRAND.name() + ": " + requestDTO.getBrandId());
                }
            }

            brandName = model.getBrand().getName();
            modelName = model.getName();
        } else if (requestDTO.getBrandId() != null) {
            boolean ok = categoryBrandRepository.existsByCategory_IdAndBrand_Id(requestDTO.getCategoryId(), requestDTO.getBrandId());
            if (!ok) throw new CustomBusinessException(ErrorCode.MODEL_NOT_BELONG_TO_BRAND.name());
            brandName = brandRepository.findById(requestDTO.getBrandId())
                    .map(Brand::getName)
                    .orElseThrow(() -> new CustomBusinessException(ErrorCode.BRAND_NOT_IN_CATEGORY.name()));
        }

        ItemType type = resolveItemType(requestDTO, model);

        if (type == ItemType.BATTERY && !"BATTERY".equalsIgnoreCase(category.getName())) {
            throw new CustomBusinessException("categoryCode must be BATTERY for itemType=BATTERY");
        }
        if (type == ItemType.VEHICLE && "BATTERY".equalsIgnoreCase(category.getName())) {
            throw new CustomBusinessException("categoryCode must not be BATTERY for itemType=VEHICLE");
        }

        ConsignmentRequest consignmentRequest = new ConsignmentRequest();
        consignmentRequest.setOwner(account);
        consignmentRequest.setItemType(type);
        consignmentRequest.setCategory(category);
        consignmentRequest.setBrand(brandName);
        consignmentRequest.setModel(modelName);
        consignmentRequest.setYear(year);
        consignmentRequest.setBatteryCapacityKwh(requestDTO.getBatteryCapacityKwh());
        consignmentRequest.setSohPercent(requestDTO.getSohPercent());
        consignmentRequest.setMileageKm(requestDTO.getMileageKm());
        consignmentRequest.setPreferredBranch(branch);
        consignmentRequest.setOwnerExpectedPrice(requestDTO.getOwnerExpectedPrice());
        consignmentRequest.setNote(requestDTO.getNote());
        consignmentRequest.setStatus(SUBMITTED);

        consignmentRequestRepository.save(consignmentRequest);

        try {
            if (images != null) {
                for (var img : images) {
                    if (img == null || img.isEmpty()) continue;
                    var stored = fileService.storeImage(img);
                    var media = new ConsignmentRequestMedia();
                    media.setRequest(consignmentRequest);
                    media.setMediaUrl(stored.getStoredName());
                    media.setMediaType(MediaType.IMAGE);
                    consignmentRequest.addMedia(media);
                }
            }

            if (videos != null) {
                for (var v : videos) {
                    if (v == null || v.isEmpty()) continue;
                    var stored = fileService.storeVideo(v);
                    var media = new ConsignmentRequestMedia();
                    media.setRequest(consignmentRequest);
                    media.setMediaUrl(stored.getStoredName());
                    media.setMediaType(MediaType.VIDEO);
                    consignmentRequest.addMedia(media);
                }
            }
        } catch (IOException e) {
            throw new CustomBusinessException("Upload media failed: " + e.getMessage());
        }

        consignmentRequestRepository.save(consignmentRequest);

        BaseResponse<Void> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(201);
        res.setMessage("Consignment request created");
        return res;
    }

    private ItemType resolveItemType(CreateConsignmentRequestDTO dto, Model model) {
        if (model != null) {
            String cate = model.getCategory().getName();
            return "BATTERY".equalsIgnoreCase(cate) ? ItemType.BATTERY : ItemType.VEHICLE;
        }

        if (dto.getItemType() != null) return dto.getItemType();

        Category cate = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CATEGORY_NOT_FOUND.name()));
        return "BATTERY".equalsIgnoreCase(cate.getName()) ? ItemType.BATTERY : ItemType.VEHICLE;
    }

    @Override
    public BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> getAll(int page, int size, String dir, String sort) {
        Pageable pageable = PageableUtils.buildPageable(page, size, sort, dir);
        Page<ConsignmentRequestProjection> pages = consignmentRequestRepository.getAll(pageable);
        PageResponse<ConsignmentRequestListItemDTO> body = toPageResponse(pages);

        return ok(body, pages.isEmpty(), ErrorCode.CONSIGNMENT_REQUEST_LIST_NOT_FOUND.name());
    }

    @Override
    public BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> getListByOwnerId(Long id, int page, int size, String dir, String sort) {
        Pageable pageable = PageableUtils.buildPageable(page, size, sort, dir);
        Page<ConsignmentRequestProjection> pages = consignmentRequestRepository.getAllByOwnerId(id, pageable);
        PageResponse<ConsignmentRequestListItemDTO> body = toPageResponse(pages);
        return ok(body, pages.isEmpty(), ErrorCode.CONSIGNMENT_REQUEST_LIST_NOT_FOUND.name() + ": " + id);
    }

    @Override
    public BaseResponse<Void> RequestAccepted(AcceptedConsignmentRequestDTO dto) {
        ConsignmentRequest request = consignmentRequestRepository.findById(dto.getId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name()));

        if (!request.getStatus().equals(SUBMITTED)) {
            throw new CustomBusinessException("request status is not eligible to be approved");
        }
        request.setStatus(SCHEDULING);
        request.setStatusChangeAt(LocalDateTime.now());
        consignmentRequestRepository.save(request);

        BaseResponse<Void> response = new BaseResponse<>();
        response.setStatus(200);
        response.setMessage("OK");
        response.setSuccess(true);
        return response;
    }

    @Override
    public BaseResponse<Void> RequestRejected(RejectedConsignmentRequestDTO dto) {
        ConsignmentRequest request = consignmentRequestRepository.findById(dto.getId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name()));

        if (!request.getStatus().equals(SUBMITTED)) {
            throw new CustomBusinessException("request status is not eligible to be rejected");
        }

        if (dto.getRejectedReason() == null || dto.getRejectedReason().trim().isEmpty()) {
            throw new CustomBusinessException("reject reason must not be blank");
        }

        request.setStatus(ConsignmentRequestStatus.REQUEST_REJECTED);
        request.setRejectedReason(dto.getRejectedReason().trim());
        request.setStatusChangeAt(LocalDateTime.now());
        consignmentRequestRepository.save(request);

        BaseResponse<Void> response = new BaseResponse<>();
        response.setStatus(200);
        response.setMessage("OK");
        response.setSuccess(true);
        return response;
    }

    @Override
    public BaseResponse<List<ConsignmentRequestListItemDTO>> getAllByBranchIdAndSubmitted(Long branchId) {
        List<ConsignmentRequestProjection> rows = consignmentRequestRepository.getAllByBranchIdAndSubmitted(branchId);
        List<ConsignmentRequestListItemDTO> body = toList(rows);
        return ok(body, body.isEmpty(), ErrorCode.CONSIGNMENT_REQUEST_LIST_NOT_FOUND.name());
    }

    @Override
    public BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> getAllByBranchIdIgnoreSubmitted(Long branchId, int page, int size, String dir, String sort) {
        Pageable pageable = PageableUtils.buildPageable(page, size, sort, dir);
        Page<ConsignmentRequestProjection> pages = consignmentRequestRepository.getAllByBranchIdIgnoreSubmitted(branchId, pageable);
        PageResponse<ConsignmentRequestListItemDTO> body = toPageResponse(pages);
        return ok(body, pages.isEmpty(), ErrorCode.CONSIGNMENT_REQUEST_LIST_NOT_FOUND.name());
    }

    @Override
    public BaseResponse<Void> setStaffForRequest(Long requestId, Long staffId) {
        if (requestId == null) throw new CustomBusinessException("consignment request id is required");
        if (staffId == null) throw new CustomBusinessException("staff id is required");

        ConsignmentRequest request = consignmentRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name()));

        Account account = accountRepository.findById(staffId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.ACCOUNT_NOT_FOUND.name()));

        BaseResponse<Void> response = new BaseResponse<>();
        if (request.getStatus().equals(SUBMITTED)) {
            request.setStaff(account);
            consignmentRequestRepository.save(request);

            response.setStatus(200);
            response.setSuccess(true);
            response.setMessage("Ok");
        }


        return response;
    }

    //lất tất cả danh sách có staff bỏ submitted
    private static final Set<ConsignmentRequestStatus> SHOWWITHOUTSUBMITTED = EnumSet.of(
            REQUEST_REJECTED,
            SCHEDULING,
            SCHEDULED,
            RESCHEDULED,
            INSPECTING,
            INSPECTED_PASS,
            INSPECTED_FAIL,
            CANCELLED,
            FINISHED,
            EXPIRED
    );

    @Override
    public BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> getListByStaffId(int page, int size, String dir, String sort) {
        Account account = authUtil.getCurrentAccount();
        Pageable pageable = PageableUtils.buildPageable(page, size, sort, dir);
        Page<ConsignmentRequestProjection> pages = consignmentRequestRepository.getAllByStaffId(account.getId(), SHOWWITHOUTSUBMITTED, pageable);
        PageResponse<ConsignmentRequestListItemDTO> body = toPageResponse(pages);
        return ok(body, pages.isEmpty(), ErrorCode.CONSIGNMENT_REQUEST_LIST_NOT_FOUND.name());
    }

    //lấy tất cả danh sách mà staff chưa duyệt
    @Override
    public BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> getListByStaffIdAndNotConsider(int page, int size, String dir, String sort) {
        Account account = authUtil.getCurrentAccount();

        Pageable pageable = PageableUtils.buildPageable(page, size, sort, dir);
        Page<ConsignmentRequestProjection> pages = consignmentRequestRepository.getAllByStaffId(account.getId(), EnumSet.of(SUBMITTED), pageable);
        PageResponse<ConsignmentRequestListItemDTO> body = toPageResponse(pages);
        return ok(body, pages.isEmpty(), ErrorCode.CONSIGNMENT_REQUEST_LIST_NOT_FOUND.name());
    }


    //user hủy request

    private static final Set<ConsignmentRequestStatus> CANCELLABLE_BEFORE_INSPECTING = EnumSet.of(
            SUBMITTED, SCHEDULING, RESCHEDULED, REQUEST_REJECTED
    );

    @Override
    public BaseResponse<Void> UserCancelRequest(CancelConsignmentRequestDTO dto) {
        Account current = authUtil.getCurrentAccount();

        ConsignmentRequest request = consignmentRequestRepository
                .findByIdAndOwnerId(dto.getRequestId(), current.getId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name()));

        if (!(request.getOwner().getId().equals(current.getId()))) {
            throw new CustomBusinessException("you don't have permission");
        }

        if (CANCELLABLE_BEFORE_INSPECTING.contains(request.getStatus())) {
            request.setStatus(ConsignmentRequestStatus.CANCELLED);
            request.setCancelledAt(LocalDateTime.now());
            request.setCancelledBy(current);
            request.setCancelledReason(dto.getCancelledReason());
            consignmentRequestRepository.save(request);
        } else throw new CustomBusinessException("Can not cancel");

        BaseResponse<Void> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("Cancelled");
        return res;

    }


    //    owner update their request
    @Transactional
    @Override
    public BaseResponse<Void> userUpdateRequest(Long requestId, UpdateConsignmentRequestDTO dto, List<MultipartFile> newImages, List<MultipartFile> newVideos) {

        Account account = authUtil.getCurrentAccount();

        ConsignmentRequest request = consignmentRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name()));

        //kiểm tra quyền
        if (!request.getOwner().getId().equals(account.getId()))
            throw new CustomBusinessException("you don't have permission");

        //kiểm tra trạng thái cho phép update
        if (!EnumSet.of(SUBMITTED, REQUEST_REJECTED).contains(request.getStatus())) {
            throw new CustomBusinessException("request is can not eligible to update");
        }


        Category targetCate = request.getCategory();
        if (dto.getCategoryId() != null) {
            targetCate = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new CustomBusinessException(ErrorCode.CATEGORY_NOT_FOUND.name()));
            if (targetCate.getStatus() != CategoryStatus.ACTIVE)
                throw new CustomBusinessException(ErrorCode.CATEGORY_INACTIVE.name());
        }

        Branch targetBranch = request.getPreferredBranch();
        if (dto.getPreferredBranchId() != null) {
            targetBranch = branchRepository.findById(dto.getPreferredBranchId())
                    .orElseThrow(() -> new CustomBusinessException(ErrorCode.BRANCH_NOT_FOUND.name()));
            if (targetBranch.getStatus() != BranchStatus.ACTIVE)
                throw new CustomBusinessException(ErrorCode.BRANCH_INACTIVE.name());
        }

        ItemType targetItemType = (dto.getItemType() != null) ? dto.getItemType() : request.getItemType();

        String targetBrandName = request.getBrand();
        String targetModelName = request.getModel();

        Model targetModel = null;

        if (dto.getModelId() != null) {
            targetModel = modelRepository.findById(dto.getModelId())
                    .orElseThrow(() -> new CustomBusinessException(ErrorCode.MODEL_NOT_FOUND.name() + ": " + dto.getModelId()));

            // Nếu có categoryId mới thì model phải thuộc category đó
            if (dto.getCategoryId() != null && !targetModel.getCategory().getId().equals(dto.getCategoryId())) {
                throw new CustomBusinessException(ErrorCode.MODEL_NOT_BELONG_TO_CATEGORY.name());
            }
            // Nếu có brandId mới thì model phải thuộc brand đó
            if (dto.getBrandId() != null && !targetModel.getBrand().getId().equals(dto.getBrandId())) {
                throw new CustomBusinessException(ErrorCode.MODEL_NOT_BELONG_TO_BRAND.name() + ": " + dto.getBrandId());
            }

            targetBrandName = targetModel.getBrand().getName();
            targetModelName = targetModel.getName();

        } else if (dto.getBrandId() != null) {
            // Khi đổi brandId (không đổi modelId), đảm bảo brand thuộc category hiện hành (mới hoặc cũ)
            Long catIdToCheck = (dto.getCategoryId() != null) ? dto.getCategoryId() : targetCate.getId();
            boolean ok = categoryBrandRepository.existsByCategory_IdAndBrand_Id(catIdToCheck, dto.getBrandId());
            if (!ok) throw new CustomBusinessException(ErrorCode.MODEL_NOT_BELONG_TO_BRAND.name());

            targetBrandName = brandRepository.findById(dto.getBrandId())
                    .map(Brand::getName)
                    .orElseThrow(() -> new CustomBusinessException(ErrorCode.BRAND_NOT_IN_CATEGORY.name()));
        }

        // Cho phép override tên hiển thị nếu người dùng nhập text brand/model
        if (dto.getBrand() != null && !dto.getBrand().isBlank()) {
            targetBrandName = dto.getBrand().trim();
        }
        if (dto.getModel() != null && !dto.getModel().isBlank()) {
            targetModelName = dto.getModel().trim();
        }

        // (Tuỳ chọn) Suy ra ItemType nếu không gửi itemType nhưng có modelId mới
        // Ví dụ: nếu category của model là "BATTERY" => ItemType.BATTERY
        if (dto.getItemType() == null && targetModel != null) {
            String catNameOfModel = targetModel.getCategory().getName();
            if ("BATTERY".equalsIgnoreCase(catNameOfModel)) {
                targetItemType = ItemType.BATTERY;
            } else {
                targetItemType = ItemType.VEHICLE;
            }
        }

        // 6) Ràng buộc ItemType ↔ Category "BATTERY"
        // (nếu bạn có CategoryCode enum thì nên dùng code thay vì name)
        if (targetItemType == ItemType.BATTERY && !"BATTERY".equalsIgnoreCase(targetCate.getName())) {
            throw new CustomBusinessException("categoryCode must be BATTERY for itemType=BATTERY");
        }
        if (targetItemType == ItemType.VEHICLE && "BATTERY".equalsIgnoreCase(targetCate.getName())) {
            throw new CustomBusinessException("categoryCode must not be BATTERY for itemType=VEHICLE");
        }

        // 7) Gán các thuộc tính cơ bản nếu có trong dto (partial)
        if (dto.getYear() != null) request.setYear(dto.getYear());
        if (dto.getBatteryCapacityKwh() != null) request.setBatteryCapacityKwh(dto.getBatteryCapacityKwh());
        if (dto.getSohPercent() != null) request.setSohPercent(dto.getSohPercent());
        if (dto.getMileageKm() != null) request.setMileageKm(dto.getMileageKm());
        if (dto.getOwnerExpectedPrice() != null) request.setOwnerExpectedPrice(dto.getOwnerExpectedPrice());
        if (dto.getNote() != null) request.setNote(dto.getNote());

        // 8) Gán các thuộc tính phụ thuộc
        request.setItemType(targetItemType);
        request.setCategory(targetCate);
        request.setPreferredBranch(targetBranch);
        request.setBrand(targetBrandName);
        request.setModel(targetModelName);
        request.setStatus(SUBMITTED);

        // 9) Xoá media (nếu có)
        if (dto.getDeleteMediaIds() != null && !dto.getDeleteMediaIds().isEmpty()) {
            List<ConsignmentRequestMedia> toRemove =
                    consignmentRequestMediaRepository.findAllByIdInAndRequest_Id(dto.getDeleteMediaIds(), request.getId());

            // Đảm bảo tất cả id yêu cầu xoá đều thuộc request (tránh id “lạ”)
            if (toRemove.size() != dto.getDeleteMediaIds().size()) {
                throw new CustomBusinessException("Some media ids do not belong to this request");
            }
            // Xoá file vật lý (tuỳ chính sách hệ thống)
            for (ConsignmentRequestMedia m : toRemove) {
                try {
                    fileService.deleteImage(m.getMediaUrl());
                } catch (Exception ignore) {
                }
                try {
                    fileService.deleteVideo(m.getMediaUrl());
                } catch (Exception ignore) {
                }
            }
            consignmentRequestMediaRepository.deleteAll(toRemove);
            request.getMediaList().removeAll(toRemove);
        }

        // 10) Thêm media mới
        try {
            if (newImages != null) {
                for (MultipartFile img : newImages) {
                    if (img == null || img.isEmpty()) continue;
                    var stored = fileService.storeImage(img);
                    ConsignmentRequestMedia media = new ConsignmentRequestMedia();
                    media.setRequest(request);
                    media.setMediaUrl(stored.getStoredName());
                    media.setMediaType(MediaType.IMAGE);
                    request.addMedia(media);
                }
            }
            if (newVideos != null) {
                for (MultipartFile v : newVideos) {
                    if (v == null || v.isEmpty()) continue;
                    var stored = fileService.storeVideo(v);
                    ConsignmentRequestMedia media = new ConsignmentRequestMedia();
                    media.setRequest(request);
                    media.setMediaUrl(stored.getStoredName());
                    media.setMediaType(MediaType.VIDEO);
                    request.addMedia(media);
                }
            }
        } catch (IOException e) {
            throw new CustomBusinessException("Upload media failed: " + e.getMessage());
        }

        // 11) Lưu
        consignmentRequestRepository.save(request);

        BaseResponse<Void> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("Consignment request updated");
        return res;


    }

    public BaseResponse<ConsignmentRequestListItemDTO> getRequestById(Long requestId) {
        if (requestId == null) throw new CustomBusinessException("request id is required");

        ConsignmentRequestProjection p = consignmentRequestRepository.getRequestById(requestId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name()));

        List<String> urls = getAllImageUrlsOfRequest(requestId);
        ConsignmentRequestListItemDTO c = ConsignmentRequestListItemDTO.builder()
                .id(p.getId())
                .accountPhone(p.getAccountPhone())
                .accountName(p.getAccountName())
                .staffId(p.getStaffId())
                .rejectedReason(p.getRejectedReason())
                .itemType(p.getItemType())
                .category(p.getCategory())
                .brand(p.getBrand())
                .model(p.getModel())
                .year(p.getYear())
                .batteryCapacityKwh(p.getBatteryCapacityKwh())
                .sohPercent(p.getSohPercent())
                .mileageKm(p.getMileageKm())
                .preferredBranchName(p.getPreferredBranchName())
                .ownerExpectedPrice(p.getOwnerExpectedPrice())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .cancelledAt(p.getCancelledAt())
                .cancelledReason(p.getCancelledReason())
                .cancelledById(p.getCancelledById())
                .mediaUrls(urls)
                .build();

        BaseResponse<ConsignmentRequestListItemDTO> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("Cancelled");
        res.setData(c);
        return res;

    }

    //=========================HELPER============================

    private List<String> getAllImageUrlsOfRequest(Long requestId) {
        List<Object[]> rows = consignmentRequestMediaRepository.findAllMediaUrlsByRequestId(requestId);
        if (rows.isEmpty()) return Collections.emptyList();

        List<String> urls = new ArrayList<>();
        for (Object[] row : rows) {
            String url = (String) row[0];
            MediaType type = (MediaType) row[1];

            if (type == MediaType.IMAGE) {
                urls.add(MedialUtils.converMediaNametoMedialUrl(url, type.name()));
            }
        }
        return urls;
    }


    //media url
    private Map<Long, List<String>> enrichMedia(List<Long> requestIds) {
        if (requestIds == null || requestIds.isEmpty()) return Collections.emptyMap();

        List<Object[]> pairs = consignmentRequestMediaRepository.findAllMediaUrlsByRequestIds(requestIds);
        // pairs: [request_id(Long), media_url(String), media_type(Enum/MediaType)]
        Map<Long, List<String>> mediaMap = new HashMap<>(pairs.size());
        for (Object[] row : pairs) {
            Long rid = (Long) row[0];
            String url = (String) row[1];
            MediaType type = (MediaType) row[2];
            mediaMap
                    .computeIfAbsent(rid, k -> new ArrayList<>())
                    .add(MedialUtils.converMediaNametoMedialUrl(url, type.name()));
        }
        return mediaMap;
    }


    //list request items
    private List<ConsignmentRequestListItemDTO> toList(List<ConsignmentRequestProjection> rows) {

        List<Long> ids = rows.stream().map(ConsignmentRequestProjection::getId).toList();
        Map<Long, List<String>> mediaMap = enrichMedia(ids);

        List<ConsignmentRequestListItemDTO> items = rows.stream()
                .map(p -> ConsignmentRequestListItemDTO.builder()
                        .id(p.getId())
                        .accountPhone(p.getAccountPhone())
                        .accountName(p.getAccountName())
                        .staffId(p.getStaffId())
                        .rejectedReason(p.getRejectedReason())
                        .itemType(p.getItemType())
                        .category(p.getCategory())
                        .brand(p.getBrand())
                        .model(p.getModel())
                        .year(p.getYear())
                        .batteryCapacityKwh(p.getBatteryCapacityKwh())
                        .sohPercent(p.getSohPercent())
                        .mileageKm(p.getMileageKm())
                        .preferredBranchName(p.getPreferredBranchName())
                        .ownerExpectedPrice(p.getOwnerExpectedPrice())
                        .status(p.getStatus())
                        .createdAt(p.getCreatedAt())
                        .cancelledAt(p.getCancelledAt())
                        .cancelledReason(p.getCancelledReason())
                        .cancelledById(p.getCancelledById())
                        .mediaUrls(mediaMap.getOrDefault(p.getId(), List.of()))
                        .build())
                .collect(Collectors.toList());
        return items;
    }


    //page response request
    private PageResponse<ConsignmentRequestListItemDTO> toPageResponse(Page<ConsignmentRequestProjection> pages) {
        List<ConsignmentRequestProjection> rows = pages.getContent();

        List<Long> ids = rows.stream().map(ConsignmentRequestProjection::getId).toList();
        Map<Long, List<String>> mediaMap = enrichMedia(ids);

        List<ConsignmentRequestListItemDTO> items = rows.stream()
                .map(p -> ConsignmentRequestListItemDTO.builder()
                        .id(p.getId())
                        .accountPhone(p.getAccountPhone())
                        .accountName(p.getAccountName())
                        .staffId(p.getStaffId())
                        .rejectedReason(p.getRejectedReason())
                        .itemType(p.getItemType())
                        .category(p.getCategory())
                        .brand(p.getBrand())
                        .model(p.getModel())
                        .year(p.getYear())
                        .batteryCapacityKwh(p.getBatteryCapacityKwh())
                        .sohPercent(p.getSohPercent())
                        .mileageKm(p.getMileageKm())
                        .preferredBranchName(p.getPreferredBranchName())
                        .ownerExpectedPrice(p.getOwnerExpectedPrice())
                        .status(p.getStatus())
                        .createdAt(p.getCreatedAt())
                        .cancelledAt(p.getCancelledAt())
                        .cancelledReason(p.getCancelledReason())
                        .cancelledById(p.getCancelledById())
                        .mediaUrls(mediaMap.getOrDefault(p.getId(), List.of()))
                        .build())
                .collect(Collectors.toList());

        PageResponse<ConsignmentRequestListItemDTO> pr = new PageResponse<>();
        pr.setTotalElements(pages.getTotalElements());
        pr.setTotalPages(pages.getTotalPages());
        pr.setHasNext(pages.hasNext());
        pr.setHasPrevious(pages.hasPrevious());
        pr.setPage(pages.getNumber());
        pr.setSize(pages.getSize());
        pr.setItems(items);
        return pr;
    }

    private static <T> BaseResponse<T> ok(T data, boolean empty, String notFoundErr) {
        BaseResponse<T> res = new BaseResponse<>();
        res.setData(data);
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage(empty ? notFoundErr : "OK");
        return res;
    }

}