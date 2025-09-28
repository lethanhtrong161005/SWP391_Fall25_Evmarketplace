package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.category;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.brand.BrandWithModelsDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryTreeDTO {
    private Long id;
    private String name;
    private String description;
    private List<BrandWithModelsDTO> brands = new ArrayList<>();
}
