package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.CreateConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.CategoryCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ErrorCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConsignmentImp implements ConsignmentService {

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


    @Override
    public BaseResponse<Void> createConsignmentRequest(CreateConsignmentRequestDTO requestDTO) {
        //acc
        Optional<Account> optAcc = accountRepository.findByPhoneNumber(requestDTO.getPhoneNumber());
        if (optAcc.isEmpty()) throw new CustomBusinessException(ErrorCode.ACCOUNT_NOT_FOUND.name());
        Account account = optAcc.get();
        //cate
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CATEGORY_NOT_FOUND.name()));
        //branch
        Branch branch = branchRepository.findById(requestDTO.getPreferredBranchId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.BRANCH_NOT_FOUND.name()));

        String brandName = requestDTO.getBrand();
        String modelName = requestDTO.getModel();
        int year = requestDTO.getYear();

        //type, intended for
        ItemType type = "BATTERY".equalsIgnoreCase(category.getName()) ? ItemType.BATTERY : ItemType.VEHICLE;
        CategoryCode intendedFor = requestDTO.getIntendedFor();

        if(type.equals(ItemType.BATTERY)){
            intendedFor = null;
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
        consignmentRequest.setStatus(requestDTO.getStatus());

        consignmentRequestRepository.save(consignmentRequest);

        BaseResponse<Void> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(201);
        res.setMessage("Consignment request created");
        return res;
    }
}
