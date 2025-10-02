package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.model;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Model;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ModelStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateModelRequest {
    private String name;
    private Integer year;
    private Long categoryId;
    private Long brandId;
    @NotNull
    private ModelStatus status;
}
