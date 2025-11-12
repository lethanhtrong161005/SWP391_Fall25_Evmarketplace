package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentInspection;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspection.CreateInspectionDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentInspection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentInspectionResult;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentRequestStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ErrorCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConsignmentRequestRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConsignmentInspectionRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentInspectionProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Service
public class ConsignmentInspectionServiceImp implements ConsignmentInspectionService {
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private ConsignmentRequestRepository consignmentRequestRepository;
    @Autowired
    private ConsignmentInspectionRepository consignmentInspectionRepository;


    @Transactional
    @Override
    public BaseResponse<Void> createInspection(CreateInspectionDTO dto) {
        Account account = authUtil.getCurrentAccount();

        ConsignmentRequest request = consignmentRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name()));

        //check staff thuộc request
        if(!request.getStaff().getId().equals(account.getId())) throw new CustomBusinessException("you don't have permission");

        EnumSet<ConsignmentRequestStatus> ALLOWED_STATUSES_FOR_INSPECTION = EnumSet.of(
                ConsignmentRequestStatus.INSPECTING,
                ConsignmentRequestStatus.INSPECTED_FAIL,
                ConsignmentRequestStatus.INSPECTED_PASS);

        //check request có đủ điều kiện để tạo inspection
        if (!(ALLOWED_STATUSES_FOR_INSPECTION.contains(request.getStatus())))
            throw new CustomBusinessException("this request can not create inspection's form");

        //check có inspection nào đang hoạt động không
        boolean isExist = consignmentInspectionRepository.existsByRequestIdAndIsActiveTrue(request.getId());
        if (isExist) throw new CustomBusinessException("the request had inspection is active");

        ConsignmentInspection inspection = new ConsignmentInspection();

        inspection.setRequest(request);
        inspection.setBranch(request.getPreferredBranch());
        inspection.setInspectionSummary(
                dto.getInspectionSummary() == null ? null : dto.getInspectionSummary().trim()
        );
        inspection.setResult(dto.getResult());
        inspection.setSuggestedPrice(dto.getSuggestedPrice());


        ConsignmentInspectionResult status = dto.getResult();
        inspection.setActive(status.equals(ConsignmentInspectionResult.PASS));

        //request
        request.setStatus(status.equals(ConsignmentInspectionResult.PASS)
                ? ConsignmentRequestStatus.INSPECTED_PASS
                : ConsignmentRequestStatus.INSPECTED_FAIL);

        consignmentRequestRepository.save(request);
        consignmentInspectionRepository.save(inspection);


        BaseResponse<Void> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("OK");
        return response;
    }

    @Transactional
    @Override
    public BaseResponse<Void> inactiveInspection(Long inspectionId) {

        //inspection
        ConsignmentInspection inspection = consignmentInspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.INSPECTION_NOT_FOUND.name()));

        //request
        ConsignmentRequest request = consignmentRequestRepository.findById(inspection.getRequest().getId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name()));

        if (!inspection.isActive()) {
            throw new CustomBusinessException("Inspection already inactive");
        }

        //kiểm tra inspection có trong hợp đồng không
        if (request.getStatus().equals(ConsignmentRequestStatus.SIGNED))
            throw new CustomBusinessException("Can not inactive this inspection");

        inspection.setActive(false);
        request.setStatus(ConsignmentRequestStatus.INSPECTING);

        consignmentInspectionRepository.save(inspection);
        consignmentRequestRepository.save(request);

        BaseResponse<Void> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("OK");
        return response;
    }

    @Override
    public BaseResponse<List<ConsignmentInspectionProjection>> searchByOwnerPhone(String phone) {

        List<ConsignmentInspectionProjection> list = consignmentInspectionRepository.searchByOwnerPhone(phone);

        BaseResponse<List<ConsignmentInspectionProjection>> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage(list.isEmpty() ? "Empty list" : "OK");
        response.setData(list);
        return response;
    }

    @Override
    public BaseResponse<List<ConsignmentInspectionProjection>> staffSearchByOwnerPhone(String phone) {
        Account account = authUtil.getCurrentAccount();
        List<ConsignmentInspectionProjection> list = consignmentInspectionRepository.staffSearchByOwnerPhone(phone, account.getId());

        BaseResponse<List<ConsignmentInspectionProjection>> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage(list.isEmpty() ? "Empty list" : "OK");
        response.setData(list);
        return response;
    }

    @Override
    public BaseResponse<ConsignmentInspectionProjection> getInspectionByRequestId(Long requestId) {
        if (requestId == null) throw new CustomBusinessException("request id is required");

        ConsignmentInspectionProjection inspectionProjection = consignmentInspectionRepository.findActiveViewByRequestId(requestId);
        if (inspectionProjection == null) throw new CustomBusinessException(ErrorCode.INSPECTION_NOT_FOUND.name());


        BaseResponse<ConsignmentInspectionProjection> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("OK");
        response.setData(inspectionProjection);
        return response;
    }

    @Override
    public BaseResponse<List<ConsignmentInspectionProjection>> findAllViewsByStatus(Collection<ConsignmentInspectionResult> statuses, Boolean isActive) {

        List<ConsignmentInspectionProjection> list = consignmentInspectionRepository.findAllViewsByStatus(statuses, isActive);

        BaseResponse<List<ConsignmentInspectionProjection>> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage(list.isEmpty() ? "Empty list" : "OK");
        response.setData(list);
        return response;
    }

    @Override
    public BaseResponse<List<ConsignmentInspectionProjection>> getListInspectionByStaffId() {
        Account account = authUtil.getCurrentAccount();

        List<ConsignmentInspectionProjection> list = consignmentInspectionRepository.findActiveViewByStaffId(account.getId());

        BaseResponse<List<ConsignmentInspectionProjection>> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage(list.isEmpty() ? "Empty list" : "OK");
        response.setData(list);
        return response;
    }


}
