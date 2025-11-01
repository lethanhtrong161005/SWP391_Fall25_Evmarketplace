package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentAgreement;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.agree.CreateAgreementDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.agreement.ConsignmentAgreementDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AgreementDuration;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentAgreementProjection;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ConsignmentAgreementService {
    BaseResponse<Void> createAgreement(CreateAgreementDTO dto, MultipartFile file);
    BaseResponse<ConsignmentAgreementDTO> getAgreementByRequestId(Long requestId);
    BaseResponse<List<ConsignmentAgreementDTO>> getAllAgreements();
    BaseResponse<Void> cancelAgreement(Long agreementId);
    BaseResponse<Void> updateAgreement(Long agreementId, AgreementDuration duration);
}
