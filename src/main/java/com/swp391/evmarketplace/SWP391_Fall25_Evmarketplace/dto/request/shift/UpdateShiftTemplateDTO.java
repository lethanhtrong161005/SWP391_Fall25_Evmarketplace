package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import lombok.Data;

import java.time.LocalTime;

@Data
public class UpdateShiftTemplateDTO {
    private String name;
    private ItemType itemType;
    private Long branchId; // nullable to detach
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;
    private Boolean isActive;
}
