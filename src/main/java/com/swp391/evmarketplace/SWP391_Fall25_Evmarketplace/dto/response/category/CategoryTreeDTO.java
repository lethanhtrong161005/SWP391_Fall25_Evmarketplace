package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.category;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.brand.BrandWithModelsDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeDTO {
    private Long id;
    private String name;
    private String description;
    private String status;
    private List<BrandWithModelsDTO> brands = new ArrayList<>();
}
