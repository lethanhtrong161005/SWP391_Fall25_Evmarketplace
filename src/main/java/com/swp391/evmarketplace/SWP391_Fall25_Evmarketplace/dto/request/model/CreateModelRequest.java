package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateModelRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull
    @Min(2000)
    private Integer year;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Brand is required")
    private Long brandId;
}
