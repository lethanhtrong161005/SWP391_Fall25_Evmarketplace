package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspectionSchedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CancelScheduleDTO {
    @NotBlank
    private String reason;
}
