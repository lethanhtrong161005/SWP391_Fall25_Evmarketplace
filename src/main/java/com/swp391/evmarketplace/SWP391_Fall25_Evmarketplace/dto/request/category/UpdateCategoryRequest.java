package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.category;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.CategoryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryRequest {
    private String name;
    private String description;
    private CategoryStatus status;
}
