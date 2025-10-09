package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.CreateConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.UpdateSetScheduleRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentRequestProject;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.PageableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ConsignmentRequestServiceImp implements ConsignmentRequestService {

    @Autowired
    private AccountRepository accountRepository;
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


    @Transactional
    @Override
    public BaseResponse<Void> createConsignmentRequest(CreateConsignmentRequestDTO requestDTO, Account account) {
        //cate
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CATEGORY_NOT_FOUND.name()));
        //branch
        Branch branch = branchRepository.findById(requestDTO.getPreferredBranchId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.BRANCH_NOT_FOUND.name()));

        if (branch.getStatus() != BranchStatus.ACTIVE)
            throw new CustomBusinessException(ErrorCode.BRANCH_INACTIVE.name());

        String brandName = requestDTO.getBrand();
        String modelName = requestDTO.getModel();
        Integer year = requestDTO.getYear();

        // type, intended for
        ItemType type = "BATTERY".equalsIgnoreCase(category.getName()) ? ItemType.BATTERY : ItemType.VEHICLE;
        // intendedFor chỉ áp dụng cho BATTERY, còn lại để null
        CategoryCode intendedFor = (type == ItemType.BATTERY) ? requestDTO.getIntendedFor() : null;
        if (type == ItemType.BATTERY && requestDTO.getIntendedFor() == null) {
            throw new CustomBusinessException(ErrorCode.INTENDED_FOR_REQUIRED.name());
        }


        //model, category, brand
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
            // Lấy tên brand chuẩn từ DB khi người dùng chọn brandId
            brandName = brandRepository.findById(requestDTO.getBrandId())
                    .map(Brand::getName)
                    .orElseThrow(() -> new CustomBusinessException(ErrorCode.BRAND_NOT_IN_CATEGORY.name()));
        }

        ConsignmentRequest consignmentRequest = new ConsignmentRequest();
        consignmentRequest.setOwner(account);
        consignmentRequest.setItemType(type);
        consignmentRequest.setCategory(category);
        consignmentRequest.setIntendedFor(intendedFor);
        consignmentRequest.setBrand(brandName);
        consignmentRequest.setModel(modelName);
        consignmentRequest.setYear(year);
        consignmentRequest.setBatteryCapacityKwh(requestDTO.getBatteryCapacityKwh());
        consignmentRequest.setSohPercent(requestDTO.getSohPercent());
        consignmentRequest.setMileageKm(requestDTO.getMileageKm());
        consignmentRequest.setPreferredBranch(branch);
        consignmentRequest.setAppointmentTime(requestDTO.getAppointmentTime());
        consignmentRequest.setOwnerExpectedPrice(requestDTO.getOwnerExpectedPrice());
        consignmentRequest.setNote(requestDTO.getNote());
        consignmentRequest.setStatus(ConsignmentRequestStatus.SUBMITTED);

        consignmentRequestRepository.save(consignmentRequest);

        BaseResponse<Void> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(201);
        res.setMessage("Consignment request created");
        return res;
    }

    @Override
    public BaseResponse<PageResponse<ConsignmentRequestProject>> getAll(int page, int size, String dir, String sort) {
        Pageable pageable = PageableUtils.buildPageable(page, size, sort, dir);
        Page<ConsignmentRequestProject> lists = consignmentRequestRepository.getAll(pageable);

        PageResponse<ConsignmentRequestProject> pageResponse = new PageResponse<>();
        pageResponse.setTotalElements(lists.getTotalElements());
        pageResponse.setTotalPages(lists.getTotalPages());
        pageResponse.setHasNext(lists.hasNext());
        pageResponse.setHasPrevious(lists.hasPrevious());
        pageResponse.setPage(lists.getNumber());
        pageResponse.setSize(lists.getSize());
        pageResponse.setItems(lists.getContent());

        BaseResponse<PageResponse<ConsignmentRequestProject>> response = new BaseResponse<>();
        response.setData(pageResponse);
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage(lists.isEmpty() ? ErrorCode.CONSIGNMENT_REQUEST_LIST_NOT_FOUND.name() : "Ok");

        return response;
    }

    @Override
    public BaseResponse<PageResponse<ConsignmentRequestProject>> getListById(Long id, int page, int size, String dir, String sort) {

        Pageable pageable = PageableUtils.buildPageable(page, size, sort, dir);
        Page<ConsignmentRequestProject> lists = consignmentRequestRepository.getAllByID(id, pageable);

        PageResponse<ConsignmentRequestProject> pageResponse = new PageResponse<>();
        pageResponse.setTotalElements(lists.getTotalElements());
        pageResponse.setTotalPages(lists.getTotalPages());
        pageResponse.setHasNext(lists.hasNext());
        pageResponse.setHasPrevious(lists.hasPrevious());
        pageResponse.setPage(lists.getNumber());
        pageResponse.setSize(lists.getSize());
        pageResponse.setItems(lists.getContent());

        BaseResponse<PageResponse<ConsignmentRequestProject>> response = new BaseResponse<>();
        response.setData(pageResponse);
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage(lists.isEmpty()
                ? (ErrorCode.CONSIGNMENT_REQUEST_LIST_NOT_FOUND.name() + ": " + id) : "Ok");

        return response;
    }

    @Override
    public BaseResponse<Void> setRequestSchedule(UpdateSetScheduleRequestDTO dto) {

        Optional<ConsignmentRequest> request = consignmentRequestRepository.findById(dto.getId());
        if (request.isEmpty()) throw new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name());
        ConsignmentRequest consignmentRequest = request.get();
        consignmentRequest.setAppointmentTime(dto.getAppointmentTime());
        consignmentRequestRepository.save(consignmentRequest);

        BaseResponse<Void> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("Set time successfully");
        return response;
    }
}
