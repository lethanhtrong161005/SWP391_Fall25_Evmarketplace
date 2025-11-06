package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentSettlement;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.StoredContractResult;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentAgreement;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentSettlement;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentAgreementStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentRequestStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.SettlementMethod;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.SettlementStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConsignmentAgreementRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConsignmentRequestRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConsignmentSettlementRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsignmentSettlementServiceImp implements ConsignmentSettlementService {
    @Autowired
    ConsignmentSettlementRepository consignmentSettlementRepository;
    @Autowired
    ConsignmentRequestRepository consignmentRequestRepository;
    @Autowired
    ConsignmentAgreementRepository consignmentAgreementRepository;

    @Autowired
    FileService fileService;

    @Override
    public BaseResponse<List<ConsignmentSettlement>> getAll() {

        List<ConsignmentSettlement> list = consignmentSettlementRepository.findAll();
        BaseResponse<List<ConsignmentSettlement>> response = new BaseResponse<>();
        response.setData(list);
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage(list.isEmpty() ? "Empty" : "OK");

        return response;
    }

    @Override
    public BaseResponse<ConsignmentSettlement> getById(Long id) {
        if (id == null) throw new CustomBusinessException("Settlement is required");

        ConsignmentSettlement c = consignmentSettlementRepository.findById(id)
                .orElseThrow(() -> new CustomBusinessException("NOT_FOUND_SETTLEMENT"));

        BaseResponse<ConsignmentSettlement> response = new BaseResponse<>();
        response.setData(c);
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("OK");
        return response;
    }

    @Override
    public BaseResponse<List<ConsignmentSettlement>> getListWithoutPayout() {

        List<ConsignmentSettlement> list = consignmentSettlementRepository.findByStatus(SettlementStatus.PENDING);
        BaseResponse<List<ConsignmentSettlement>> response = new BaseResponse<>();
        response.setData(list);
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage(list.isEmpty() ? "Empty" : "OK");

        return response;
    }

    @Transactional
    @Override
    public BaseResponse<Void> setPayout(Long settlementId, MultipartFile file) {

        if (settlementId == null) throw new CustomBusinessException("settlement id required");

        ConsignmentSettlement settlement = consignmentSettlementRepository.findById(settlementId)
                .orElseThrow(() -> new CustomBusinessException("NOT_FOUND_SETTLEMENT"));

        ConsignmentAgreement agreement = consignmentAgreementRepository.findById(settlement.getAgreementId())
                .orElseThrow(() -> new CustomBusinessException("Not found agreement of this settlement id: " + settlementId));

        ConsignmentRequest request = consignmentRequestRepository.findById(agreement.getRequest().getId())
                .orElseThrow(() -> new CustomBusinessException("Not found request of this settlement id: " + settlementId));

        if (!settlement.getStatus().equals(SettlementStatus.PENDING))
            throw new CustomBusinessException("No condition to update payout");

        StoredContractResult result;
        try {
            result = fileService.storedContract(file);
        } catch (IOException e) {
            throw new CustomBusinessException("Error while uploading file: " + e.getMessage());
        }
        settlement.setMediaUrl(result.getFileName());
        settlement.setMethod(SettlementMethod.CASH);
        settlement.setPaidAt(LocalDateTime.now());
        settlement.setStatus(SettlementStatus.PAID);

        agreement.setStatus(ConsignmentAgreementStatus.FINISHED);

        request.setStatus(ConsignmentRequestStatus.FINISHED);

        consignmentSettlementRepository.save(settlement);
        consignmentRequestRepository.save(request);
        consignmentAgreementRepository.save(agreement);

        BaseResponse<Void> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("OK");
        return response;
    }
}
