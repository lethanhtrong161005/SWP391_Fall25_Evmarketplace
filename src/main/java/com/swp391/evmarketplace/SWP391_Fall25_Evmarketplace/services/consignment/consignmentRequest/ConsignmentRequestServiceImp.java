package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentRequest;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.AcceptedConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.CreateConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.RejectedConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.UpdateConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.ConsignmentRequestListItemDTO;
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

        List<Long> ids = rows.stream().map(ConsignmentRequestProjection::getId).toList();
        List<Object[]> mediaPairs = ids.isEmpty() ? List.of() : consignmentRequestMediaRepository.findAllMediaUrlsByRequestIds(ids);
        Map<Long, List<String>> mediaMap = new HashMap<>();
        for (Object[] pair : mediaPairs) {
            Long rid = (Long) pair[0];
            String url = (String) pair[1];
            MediaType type = (MediaType) pair[2];
            mediaMap.computeIfAbsent(rid, k -> new ArrayList<>()).add(MedialUtils.converMediaNametoMedialUrl(url, type.name(), serverUrl));
        }

        List<ConsignmentRequestListItemDTO> items = new ArrayList<>();
        for (ConsignmentRequestProjection p : rows) {
            items.add(ConsignmentRequestListItemDTO.builder()
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
                    .mediaUrls(mediaMap.getOrDefault(p.getId(), List.of()))
                    .build());
        }


        BaseResponse<List<ConsignmentRequestListItemDTO>> response = new BaseResponse<>();
        response.setData(items);
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage(rows.isEmpty() ? "List Empty" : "Ok");
        return response;
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

    //lất tất cả danh sách có staff
    @Override
    public BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> getListByStaffId(int page, int size, String dir, String sort) {
        Account account = authUtil.getCurrentAccount();
        Pageable pageable = PageableUtils.buildPageable(page, size, sort, dir);
        Page<ConsignmentRequestProjection> pages = consignmentRequestRepository.getAllByStaffId(account.getId(), EnumSet.allOf(ConsignmentRequestStatus.class), pageable);
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
            SUBMITTED, SCHEDULING, SCHEDULED, RESCHEDULED
    );

    @Override
    public BaseResponse<Void> UserCancelRequest(Long requestId) {
        if (requestId == null) {
            throw new CustomBusinessException("consignment request id is required");
        }

        Account current = authUtil.getCurrentAccount();
        ConsignmentRequest request = consignmentRequestRepository
                .findByIdAndOwnerId(requestId, current.getId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name()));

        if (!(current.getRole().equals(AccountRole.MEMBER)) || request.getStatus() == INSPECTING) {
            throw new CustomBusinessException("cannot cancel this state");
        }

        if (CANCELLABLE_BEFORE_INSPECTING.contains(request.getStatus())) {
            request.setStatus(ConsignmentRequestStatus.CANCELLED);
            // cancel by, at, reason
            // cancel scheduled
            consignmentRequestRepository.save(request);
        }


        BaseResponse<Void> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("Cancelled");
        return res;

    }

    //owner update their request
//    @Override
//    public BaseResponse<Void> updateRequest(Long requestId, UpdateConsignmentRequestDTO dto) {
//        ConsignmentRequest request = consignmentRequestRepository.findById(requestId)
//                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name()));
//
//        if(!EnumSet.of(SUBMITTED, RESCHEDULED).contains(request.getStatus())){
//            throw new CustomBusinessException("request is can not eligible to update");
//        }
//
//        Category category = categoryRepository.findById(dto.getId())
//                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CATEGORY_NOT_FOUND.name()));
//
//
//
//
//
//
//
//
//
//
//
//    }


    //=========================HELPER============================

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
                    .add(MedialUtils.converMediaNametoMedialUrl(url, type.name(), serverUrl));
        }
        return mediaMap;
    }

    //list request
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

