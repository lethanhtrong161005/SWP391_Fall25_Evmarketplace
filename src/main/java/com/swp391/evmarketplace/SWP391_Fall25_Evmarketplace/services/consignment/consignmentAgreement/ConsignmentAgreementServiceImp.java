package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentAgreement;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.agree.CreateAgreementDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
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
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ConsignmentAgreementServiceImp implements ConsignmentAgreementService {
    @Autowired
    AuthUtil authUtil;
    @Autowired
    ConsignmentAgreementRepository consignmentAgreementRepository;
    @Autowired
    ConsignmentRequestRepository consignmentRequestRepository;
    @Autowired
    ConsignmentInspectionRepository consignmentInspectionRepository;


    @Transactional
    @Override
    public BaseResponse<Void> createAgreement(CreateAgreementDTO dto) {
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

        //agreement deposit
        // depositAmount = acceptablePrice * (depositPercent / 100)
        BigDecimal depositAmount = dto.getAcceptablePrice()
                .multiply(dto.getDepositPercent())
                .divide(new BigDecimal("100"));


        ConsignmentAgreement agreement = new ConsignmentAgreement();
        agreement.setRequest(request);
        agreement.setOwner(request.getOwner());
        agreement.setBranch(request.getPreferredBranch());
        agreement.setStaff(account);

        agreement.setCommissionPercent(dto.getCommissionPercent());
        agreement.setAcceptablePrice(dto.getAcceptablePrice());

        agreement.setDepositPercent(dto.getDepositPercent());
        agreement.setDepositAmount(depositAmount);
        agreement.setDepositStatus(DepositStatus.PAID);

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
    public BaseResponse<ConsignmentAgreementProjection> getAgreementByRequestId(Long requestId) {
        if (requestId == null) throw new CustomBusinessException("request is is required");

        ConsignmentAgreementProjection agreement = consignmentAgreementRepository
                .findProjectionByRequestId(requestId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.AGREEMENT_NOT_FOUND.name()));

        BaseResponse<ConsignmentAgreementProjection> res = new BaseResponse<>();
        res.setData(agreement);
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("OK");
        return res;
    }

    @Override
    public BaseResponse<List<ConsignmentAgreementProjection>> getAllAgreements() {
        List<ConsignmentAgreementProjection> list = consignmentAgreementRepository.findAllProjections();

        BaseResponse<List<ConsignmentAgreementProjection>> res = new BaseResponse<>();
        res.setData(list);
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
        agreement.setDepositStatus(DepositStatus.FORFEITED);
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
        if(agreementId == null)
            throw new CustomBusinessException("agreement id is required");

        ConsignmentAgreement agreement = consignmentAgreementRepository.findById(agreementId)
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.AGREEMENT_NOT_FOUND.name()));

        if(!agreement.getStatus().equals(ConsignmentAgreementStatus.EXPIRED))
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


}
