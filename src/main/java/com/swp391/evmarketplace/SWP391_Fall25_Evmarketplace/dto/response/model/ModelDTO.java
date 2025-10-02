package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.model;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Brand;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Category;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDTO {
    private Long id;
    private String name;
    private Integer year;
    private String status;

    private Long brandId;
    private String brandName;

    private Long categoryId;
    private String categoryName;


    public ModelDTO(Long id, String name, Integer year) {
        this.id = id;
        this.name = name;
        this.year = year;
    }

}
