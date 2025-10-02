package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.brand;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Brand;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponseDTO {
    private Long id;
    private String name;
    private String status;
    private List<Long> categoryIds;

    public BrandResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
