package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalTime;

@Data
public class CreateShiftTemplateDTO {
    @NotBlank
    @Size(max = 32)
    private String code;
    @NotBlank
    @Size(max = 64)
    private String name;
    @NotNull
    private ItemType itemType;
    private Long branchId; // optional
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;
    private Boolean isActive = Boolean.TRUE;
}
