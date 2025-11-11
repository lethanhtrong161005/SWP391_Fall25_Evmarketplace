package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentAgreement;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.agree.CreateAgreementDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.agreement.ConsignmentAgreementDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.StoredContractResult;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentAgreement;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentInspection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConsignmentAgreementRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConsignmentInspectionRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConsignmentRequestRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentAgreementProjection;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.MedialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ConsignmentAgreementServiceImp implements ConsignmentAgreementService {
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private ConsignmentAgreementRepository consignmentAgreementRepository;
    @Autowired
    private ConsignmentRequestRepository consignmentRequestRepository;
    @Autowired
    private ConsignmentInspectionRepository consignmentInspectionRepository;
    @Autowired
    private FileService fileService;


    @Transactional
    @Override
    public BaseResponse<Void> createAgreement(CreateAgreementDTO dto, MultipartFile file) {
        Account account = authUtil.getCurrentAccount();

        ConsignmentRequest request = consignmentRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name()));

        //chek request condition
        if (!request.getStatus().equals(ConsignmentRequestStatus.INSPECTED_PASS))
            throw new CustomBusinessException("Request is not eligible for create agreement");

        //phải là staff của request tạo
        if (!request.getStaff().getId().equals(account.getId()))
            throw new CustomBusinessException("you don't have permission");

        //check request had agreement or not?
        if (consignmentAgreementRepository.existsByRequestId(dto.getRequestId()))
            throw new CustomBusinessException("Agreement already exists for this request");

        //Check active inspection for this request
        ConsignmentInspection inspection = consignmentInspectionRepository
                .findByRequestIdAndIsActiveTrue(dto.getRequestId())
                .orElseThrow(() -> new CustomBusinessException(
                        "Active inspection not found for this request"));

        //duration
        var startAt = dto.getStartAt();
        var months = dto.getDuration().getMonths();
        var expireAt = startAt.plusMonths(months);

        //media
        StoredContractResult result;
        try {
            result = fileService.storedContract(file);
        } catch (IOException e) {
            throw new CustomBusinessException("Error while uploading file: " + e.getMessage());
        }

        ConsignmentAgreement agreement = new ConsignmentAgreement();
        agreement.setRequest(request);

        agreement.setMedialUrl(result.getFileName());

        agreement.setOwner(request.getOwner());
        agreement.setBranch(request.getPreferredBranch());
        agreement.setStaff(account);

        agreement.setCommissionPercent(dto.getCommissionPercent());
        agreement.setAcceptablePrice(dto.getAcceptablePrice());

        agreement.setStartAt(startAt);
        agreement.setDuration(dto.getDuration());
        agreement.setExpireAt(expireAt);

        agreement.setStatus(ConsignmentAgreementStatus.SIGNED);

        request.setStatus(ConsignmentRequestStatus.SIGNED);

        consignmentAgreementRepository.save(agreement);
        consignmentRequestRepository.save(request);

        BaseResponse<Void> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("OK");
        return res;
    }

    @Override
    public BaseResponse<Object> getAgreementByRequestId(Long requestId) {
        if (requestId == null) throw new CustomBusinessException("request is is required");
        var ar = consignmentAgreementRepository.findByRequest_Id(requestId).orElseThrow(() -> new CustomBusinessException("AGREEMENT NOT FOUND BY REQUEST ID" + requestId));
        boolean isCreateListing = ar.getListing() == null;
        ConsignmentAgreementProjection agreement = consignmentAgreementRepository
                .findProjectionByRequestId(requestId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.AGREEMENT_NOT_FOUND.name()));

        ConsignmentAgreementDTO c = projectionToDto(agreement);
        Map<String, Object> data = Map.of(
                "item", c,
                "isCreateListing", isCreateListing
        );
        BaseResponse<Object> res = new BaseResponse<>();
        res.setData(data);
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("OK");
        return res;
    }

    @Override
    public BaseResponse<List<ConsignmentAgreementDTO>> getAllAgreements() {
        List<ConsignmentAgreementProjection> list = consignmentAgreementRepository.findAllProjections();

        List<ConsignmentAgreementDTO> dtos = list.stream()
                .map(this::projectionToDto).toList();

        BaseResponse<List<ConsignmentAgreementDTO>> res = new BaseResponse<>();
        res.setData(dtos);
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage(list.isEmpty() ? "empty" : "OK");
        return res;
    }

    @Override
    public BaseResponse<Void> cancelAgreement(Long agreementId) {
        if (agreementId == null) throw new CustomBusinessException("agreement id is required");

        ConsignmentAgreement agreement = consignmentAgreementRepository.findById(agreementId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.AGREEMENT_NOT_FOUND.name()));

        if (!agreement.getStatus().equals(ConsignmentAgreementStatus.SIGNED))
            throw new CustomBusinessException("No condition to cancel agreement");

        agreement.setStatus(ConsignmentAgreementStatus.CANCELLED);
        consignmentAgreementRepository.save(agreement);

        //request
        ConsignmentRequest request = consignmentRequestRepository.findById(agreement.getRequest().getId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.CONSIGNMENT_REQUEST_NOT_FOUND.name()));
        request.setStatus(ConsignmentRequestStatus.CANCELLED);
        consignmentRequestRepository.save(request);

        BaseResponse<Void> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("OK");
        return res;
    }

    @Transactional
    @Override
    public BaseResponse<Void> updateAgreement(Long agreementId, AgreementDuration duration) {
        if (agreementId == null)
            throw new CustomBusinessException("agreement id is required");

        ConsignmentAgreement agreement = consignmentAgreementRepository.findById(agreementId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.AGREEMENT_NOT_FOUND.name()));

        if (!agreement.getStatus().equals(ConsignmentAgreementStatus.EXPIRED))
            throw new CustomBusinessException("no condition to update agreement");

        var startAt = agreement.getExpireAt();
        var moths = duration.getMonths();
        var expireAt = startAt.plusMonths(moths);

        //agreement
        agreement.setStartAt(startAt);
        agreement.setExpireAt(expireAt);
        agreement.setDuration(duration);
        agreement.setStatus(ConsignmentAgreementStatus.SIGNED);

        //request
        ConsignmentRequest request = agreement.getRequest();
        request.setStatus(ConsignmentRequestStatus.SIGNED);

        //save
        consignmentRequestRepository.save(request);
        consignmentAgreementRepository.save(agreement);

        BaseResponse<Void> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("OK");
        return res;
    }

    @Override
    public BaseResponse<List<ConsignmentAgreementDTO>> searchByPhone(String phone) {
        List<ConsignmentAgreement> list = consignmentAgreementRepository.findAllByOwnerPhoneNumber(phone);

        List<ConsignmentAgreementDTO> dtos = list.stream()
                .map(this::toDto).toList();

        BaseResponse<List<ConsignmentAgreementDTO>> res = new BaseResponse<>();
        res.setData(dtos);
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage(dtos.isEmpty() ? "empty" : "OK");
        return res;
    }


    //===================HELPER===================

    private ConsignmentAgreementDTO projectionToDto(ConsignmentAgreementProjection projection) {
        return ConsignmentAgreementDTO.builder()
                .id(projection.getId())
                .requestId(projection.getRequestId())
                .ownerName(projection.getOwnerName())
                .phone(projection.getOwnerPhone())
                .staffName(projection.getStaffName())
                .branchName(projection.getBranchName())
                .commissionPercent(projection.getCommissionPercent())
                .acceptablePrice(projection.getAcceptablePrice())
                .status(projection.getStatus())
                .duration(projection.getDuration())
                .medialUrl(MedialUtils.converMediaNametoMedialUrl(projection.getMedialUrl(), ""))
                .startAt(projection.getStartAt())
                .expireAt(projection.getExpireAt())
                .createdAt(projection.getCreatedAt())
                .updatedAt(projection.getUpdatedAt())
                .build();
    }

    private ConsignmentAgreementDTO toDto(ConsignmentAgreement agreement) {
        return ConsignmentAgreementDTO.builder()
                .id(agreement.getId())
                .requestId(agreement.getRequest().getId())
                .ownerName(agreement.getOwner().getProfile().getFullName())
                .phone(agreement.getOwner().getPhoneNumber())
                .staffName(agreement.getStaff().getProfile().getFullName())
                .branchName(agreement.getBranch().getName())
                .commissionPercent(agreement.getCommissionPercent())
                .acceptablePrice(agreement.getAcceptablePrice())
                .status(agreement.getStatus().name())
                .duration(agreement.getDuration().name())
                .medialUrl(MedialUtils.converMediaNametoMedialUrl(agreement.getMedialUrl(), ""))
                .startAt(agreement.getStartAt())
                .expireAt(agreement.getExpireAt())
                .createdAt(agreement.getCreatedAt())
                .updatedAt(agreement.getUpdatedAt())
                .build();
    }

//

}
