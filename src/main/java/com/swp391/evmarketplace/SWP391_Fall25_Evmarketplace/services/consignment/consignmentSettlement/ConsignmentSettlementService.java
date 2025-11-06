package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentSettlement;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ConsignmentSettlement;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ConsignmentSettlementService {
    BaseResponse<List<ConsignmentSettlement>> getAll();
    BaseResponse<ConsignmentSettlement> getById(Long id);
    BaseResponse<List<ConsignmentSettlement>> getListWithoutPayout();

    BaseResponse<Void> setPayout(Long settlementId, MultipartFile file);

}
