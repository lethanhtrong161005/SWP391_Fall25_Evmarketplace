package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.brand;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.brand.BrandResponseDTO;

import java.util.List;

public interface BrandService {
    BaseResponse<List<BrandResponseDTO>> getAllBrandByCategoryId(Long id);
}
