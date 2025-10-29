package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentInspection;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspection.CreateInspectionDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentInspectionResult;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentInspectionProjection;

import java.util.Collection;
import java.util.List;

public interface ConsignmentInspectionService {
    BaseResponse<Void> createInspection(CreateInspectionDTO dto);
    BaseResponse<ConsignmentInspectionProjection> getInspectionByRequestId(Long requestId);
    BaseResponse<List<ConsignmentInspectionProjection>> findAllViewsByStatus(Collection<ConsignmentInspectionResult> statuses, Boolean isActive);
    BaseResponse<List<ConsignmentInspectionProjection>> getListInspectionByStaffId();
    BaseResponse<Void> inactiveInspection(Long inspectionId);
}
