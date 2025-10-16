package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentRequest;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.AcceptedConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.CreateConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.RejectedConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.ConsignmentRequestListItemDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ConsignmentRequestService {
    BaseResponse<Void> createConsignmentRequest(CreateConsignmentRequestDTO requestDTO, List<MultipartFile> images, List<MultipartFile> videos);
    BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> getAll(int page, int size, String dir, String sort);
    BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> getListByOwnerId(Long id, int page, int size, String dir, String sort);

    BaseResponse<Void> RequestAccepted(AcceptedConsignmentRequestDTO requestDTO);
    BaseResponse<Void> RequestRejected(RejectedConsignmentRequestDTO requestDTO);
    BaseResponse<List<ConsignmentRequestListItemDTO>> getListByBranchIdAndStaffIsNull(Long branchId);
    BaseResponse<Void> setStaffForRequest(Long requestId, Long staffId);
}
