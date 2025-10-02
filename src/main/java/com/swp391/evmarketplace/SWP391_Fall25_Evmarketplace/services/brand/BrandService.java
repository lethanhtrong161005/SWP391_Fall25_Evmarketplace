package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.brand;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.brand.CreateBrandRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.brand.UpdateBrandRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.brand.BrandResponseDTO;

import java.util.List;

public interface BrandService {
    BaseResponse<?> getAllBrand();
    BaseResponse<?> getAllBrandByCategoryId(Long categoryId);
    BaseResponse<?> addBrand(CreateBrandRequest req);
    BaseResponse<?> updateBrand(Long id, UpdateBrandRequest req);
    BaseResponse<?> deleteBrand(Long id);
}
