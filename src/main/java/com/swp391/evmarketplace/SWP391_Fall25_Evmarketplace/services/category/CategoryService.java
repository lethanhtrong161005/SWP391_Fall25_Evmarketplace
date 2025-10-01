package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.category;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.category.CreateCategoryDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.category.UpdateCategoryRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.category.CategoryTreeDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.category.CategoryBrandWithModelsDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.category.CategoryResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.CategoryBrand;

import java.util.List;

public interface CategoryService {
    BaseResponse<List<CategoryResponseDTO>> getAll();

    BaseResponse<List<CategoryResponseDTO>> getByNameIn(List<String> names);

    List<CategoryBrand> getAllBrandWithCategoryId(Long id);


    BaseResponse<List<CategoryBrandWithModelsDTO>> getCategoryBrandsWithModels(Long categoryId);

    BaseResponse<List<CategoryTreeDTO>> getCategoryBrandModel();
    BaseResponse<CategoryTreeDTO> getCategoryBrandModelById(Long categoryId);

    BaseResponse<Void> addCategory(CreateCategoryDTO request);
    BaseResponse<?> deleteCategory(Long categoryId);

    BaseResponse<?> updateCategory(Long categoryId, UpdateCategoryRequest request);
}
