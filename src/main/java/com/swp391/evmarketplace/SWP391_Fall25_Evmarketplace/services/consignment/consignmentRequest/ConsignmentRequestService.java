package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment.consignmentRequest;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.request.ConsignmentRequestListItemDTO;
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
    BaseResponse<List<ConsignmentRequestListItemDTO>> getAllByBranchIdAndSubmitted(Long branchId);
    BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> getAllByBranchIdIgnoreSubmitted(Long branchId, int page, int size, String dir, String sort);
    BaseResponse<Void> setStaffForRequest(Long requestId, Long staffId);

    //lấy tất cả request có staff
    BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> getListByStaffId(int page, int size, String dir, String sort);
    //lấy tất cả request staff nhưng chưa xem xét
    BaseResponse<PageResponse<ConsignmentRequestListItemDTO>> getListByStaffIdAndNotConsider(int page, int size, String dir, String sort);
    BaseResponse<Void> UserCancelRequest(CancelConsignmentRequestDTO dto);
    BaseResponse<Void> userUpdateRequest(Long requestId, UpdateConsignmentRequestDTO dto, List<MultipartFile> newImages, List<MultipartFile> newVideos);
    BaseResponse<ConsignmentRequestListItemDTO> getRequestById(Long requestId);
}
