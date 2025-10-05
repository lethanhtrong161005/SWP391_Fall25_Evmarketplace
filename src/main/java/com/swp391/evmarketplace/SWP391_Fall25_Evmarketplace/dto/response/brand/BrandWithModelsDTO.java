package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.brand;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.model.ModelDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BrandWithModelsDTO {
    private Long id;
    private String name;
    private String status;
    private List<ModelDTO> models = new ArrayList<>();
}
