package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentAgreement;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.agree.CreateAgreementDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AgreementDuration;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentAgreementProjection;

import java.util.List;

public interface ConsignmentAgreementService {
    BaseResponse<Void> createAgreement(CreateAgreementDTO dto);
    BaseResponse<ConsignmentAgreementProjection> getAgreementByRequestId(Long requestId);
    BaseResponse<List<ConsignmentAgreementProjection>> getAllAgreements();
    BaseResponse<Void> cancelAgreement(Long agreementId);
    BaseResponse<Void> updateAgreement(Long agreementId, AgreementDuration duration);
}
