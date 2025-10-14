package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.shift;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.shift.CreateShiftTemplateDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.shift.UpdateShiftTemplateDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.PageResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift.ShiftTemplateResponseDTO;

public interface ShiftTemplateService {
    BaseResponse<ShiftTemplateResponseDTO> create(CreateShiftTemplateDTO dto);
    BaseResponse<ShiftTemplateResponseDTO> getById(Long id);
    BaseResponse<PageResponse<ShiftTemplateResponseDTO>> list(int page, int size, String sort, String dir);
    BaseResponse<ShiftTemplateResponseDTO> update(Long id, UpdateShiftTemplateDTO dto);
    BaseResponse<?> delete(Long id);
}
