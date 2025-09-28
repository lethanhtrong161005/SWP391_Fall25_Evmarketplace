package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.category.CategoryTreeDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.category.CategoryResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<CategoryResponseDTO>>> getAll(){
        BaseResponse<List<CategoryResponseDTO>> response = categoryService.getAll();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/all/detail")
    public ResponseEntity<?> getAllDetail(){
        BaseResponse<List<CategoryTreeDTO>> response = categoryService.getCategoryBrandModel();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<BaseResponse<List<CategoryResponseDTO>>> getByNameIn(@RequestParam List<String> names){
        BaseResponse<List<CategoryResponseDTO>> response = categoryService.getByNameIn(names);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{categoryId}/brands-with-models")
    public ResponseEntity<?> getBrandsWithModels(
            @PathVariable Long categoryId) {
        BaseResponse<CategoryTreeDTO> resp = categoryService.getCategoryBrandModelById(categoryId);
        return ResponseEntity.status(resp.getStatus()).body(resp);
    }


}
