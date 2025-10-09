package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.consignment;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.CreateConsignmentRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request.UpdateSetScheduleRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ConsignmentRequestProject;

public interface ConsignmentRequestService {
    BaseResponse<Void> createConsignmentRequest(CreateConsignmentRequestDTO requestDTO, Account account);
    BaseResponse<PageResponse<ConsignmentRequestProject>> getAll(int page, int size, String dir, String sort);
    BaseResponse<PageResponse<ConsignmentRequestProject>> getListById(Long id, int page, int size, String dir, String sort);
    BaseResponse<Void> setRequestSchedule(UpdateSetScheduleRequestDTO dto);
}
