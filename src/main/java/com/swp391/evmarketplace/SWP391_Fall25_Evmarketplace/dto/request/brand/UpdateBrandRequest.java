package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.brand;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.BrandStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBrandRequest {
    private String name;            // optional
    private BrandStatus status;          // optional: ACTIVE/HIDDEN
    private List<Long> categoryIds; // optional: nếu != null => REPLACE
}
