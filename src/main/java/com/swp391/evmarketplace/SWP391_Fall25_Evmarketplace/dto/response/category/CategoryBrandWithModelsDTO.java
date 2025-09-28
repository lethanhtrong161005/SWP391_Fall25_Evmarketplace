package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.category;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.brand.BrandResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.model.ModelDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryBrandWithModelsDTO {
    private Long id;
    private CategoryResponseDTO category;
    private BrandResponseDTO brand;
    private List<ModelDTO> models;
}
