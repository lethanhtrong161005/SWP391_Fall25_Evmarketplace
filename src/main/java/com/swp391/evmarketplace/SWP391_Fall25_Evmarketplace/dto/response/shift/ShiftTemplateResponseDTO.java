package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ShiftTemplateResponseDTO {
    private Long id;
    private String code;
    private String name;
    private ItemType itemType;
    private Long branchId;
    private String branchName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
