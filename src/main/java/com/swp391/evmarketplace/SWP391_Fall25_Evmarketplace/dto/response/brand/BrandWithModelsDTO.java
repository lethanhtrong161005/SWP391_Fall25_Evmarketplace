package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.brand;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.model.ModelDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BrandWithModelsDTO {
    private Long id;
    private String name;
    private List<ModelDTO> models = new ArrayList<>();
}
