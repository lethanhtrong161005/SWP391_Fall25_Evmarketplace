package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.brand;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.BrandStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBrandRequest {
    @NotBlank(message = "Name is required")
    private String name;

    // optional: ACTIVE/HIDDEN (nếu muốn set ngay khi tạo)
    private BrandStatus status;

    // optional: list category để gắn mapping
    private List<Long> categoryIds;
}
