package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.CreateConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;

public interface ConsignmentService {
    BaseResponse<Void> createConsignmentRequest(CreateConsignmentRequestDTO requestDTO);

}
