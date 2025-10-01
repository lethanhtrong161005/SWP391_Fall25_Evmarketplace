package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.brand;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBrandRequest {
    @NotBlank(message = "Name is required")
    private String name;
}
