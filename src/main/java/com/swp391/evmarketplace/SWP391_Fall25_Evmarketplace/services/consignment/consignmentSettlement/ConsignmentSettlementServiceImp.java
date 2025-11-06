package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentSettlement;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.StoredContractResult;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentSettlement;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.SettlementMethod;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.SettlementStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConsignmentSettlementRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsignmentSettlementServiceImp implements ConsignmentSettlementService {
    @Autowired
    ConsignmentSettlementRepository consignmentSettlementRepository;

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

    @Override
    public BaseResponse<Void> setPayout(Long settlementId, MultipartFile file) {

        if (settlementId == null) throw new CustomBusinessException("settlement id required");

        ConsignmentSettlement settlement = consignmentSettlementRepository.findById(settlementId)
                .orElseThrow(() -> new CustomBusinessException("NOT_FOUND_SETTLEMENT"));

        if (!settlement.getStatus().equals(SettlementStatus.PENDING))
            throw new CustomBusinessException("No condition to update payout");

        StoredContractResult result;
        try {
            result = fileService.storedContract(file);
        } catch (IOException e) {
            throw new CustomBusinessException("Error while uploading file: " + e.getMessage());
        }

        settlement.setMedialUrl(result.getFileName());
        settlement.setMethod(SettlementMethod.CASH);
        settlement.setPaidAt(LocalDateTime.now());
        settlement.setStatus(SettlementStatus.PAID);

        BaseResponse<Void> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("OK");
        return response;
    }
}
