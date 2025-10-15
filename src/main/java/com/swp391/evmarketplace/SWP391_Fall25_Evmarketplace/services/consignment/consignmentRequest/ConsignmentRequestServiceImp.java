package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentRequest;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.AcceptedConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.CreateConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.RejectedConsignmentRequestDTO;
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
import java.util.*;

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

        if (type == ItemType.BATTERY && !"BATTERY".equalsIgnoreCase(brandName)) {
            throw new CustomBusinessException("categoryCode must be BATTERY for itemType=BATTERY");
        }
        if (type == ItemType.VEHICLE && "BATTERY".equalsIgnoreCase(brandName)) {
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
        consignmentRequest.setStatus(ConsignmentRequestStatus.SUBMITTED);

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

        List<ConsignmentRequestProjection> rows = pages.getContent();
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

        PageResponse<ConsignmentRequestListItemDTO> pageResponse = new PageResponse<>();
        pageResponse.setTotalElements(pages.getTotalElements());
        pageResponse.setTotalPages(pages.getTotalPages());
        pageResponse.setHasNext(pages.hasNext());
        pageResponse.setHasPrevious(pages.hasPrevious());
        pageResponse.setPage(pages.getNumber());
        pageResponse.setSize(pages.getSize());
        pageResponse.setItems(items);

        BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> response = new BaseResponse<>();
        response.setData(pageResponse);
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage(pages.isEmpty() ? ErrorCode.CONSIGNMENT_REQUEST_LIST_NOT_FOUND.name() : "Ok");
        return response;
    }

    @Override
    public BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> getListById(Long id, int page, int size, String dir, String sort) {
        Pageable pageable = PageableUtils.buildPageable(page, size, sort, dir);
        Page<ConsignmentRequestProjection> pages = consignmentRequestRepository.getAllByID(id, pageable);

        List<ConsignmentRequestProjection> rows = pages.getContent();
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

        PageResponse<ConsignmentRequestListItemDTO> pageResponse = new PageResponse<>();
        pageResponse.setTotalElements(pages.getTotalElements());
        pageResponse.setTotalPages(pages.getTotalPages());
        pageResponse.setHasNext(pages.hasNext());
        pageResponse.setHasPrevious(pages.hasPrevious());
        pageResponse.setPage(pages.getNumber());
        pageResponse.setSize(pages.getSize());
        pageResponse.setItems(items);

        BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> response = new BaseResponse<>();
        response.setData(pageResponse);
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage(pages.isEmpty() ? (ErrorCode.CONSIGNMENT_REQUEST_LIST_NOT_FOUND.name() + ": " + id) : "Ok");
        return response;
    }

    @Override
    public BaseResponse<Void> RequestAccepted(AcceptedConsignmentRequestDTO dto) {
        ConsignmentRequest request = consignmentRequestRepository.findById(dto.getId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name()));

        Account account = authUtil.getCurrentAccount();

        if (request.getStatus().equals(ConsignmentRequestStatus.SUBMITTED)) {
            request.setStatus(dto.getStatus());
            request.setStaff(account);
            consignmentRequestRepository.save(request);
        } else {
            throw new CustomBusinessException("Consignment request status must be submitted");
        }

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

        Account account = authUtil.getCurrentAccount();

        if (request.getStatus().equals(ConsignmentRequestStatus.SUBMITTED)) {
            request.setStatus(dto.getStatus());
            request.setRejectedReason(dto.getRejectedReason());
            request.setStaff(account);
            consignmentRequestRepository.save(request);
        } else {
            throw new CustomBusinessException("Consignment request status must be submitted");
        }

        BaseResponse<Void> response = new BaseResponse<>();
        response.setStatus(200);
        response.setMessage("OK");
        response.setSuccess(true);
        return response;
    }


}

