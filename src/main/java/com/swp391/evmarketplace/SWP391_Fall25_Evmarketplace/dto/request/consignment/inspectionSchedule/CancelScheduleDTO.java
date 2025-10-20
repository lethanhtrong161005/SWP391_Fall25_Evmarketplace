package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.inspectionSchedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CancelScheduleDTO {
    @NotNull
    private Long cancelledBy;
    @NotBlank
    private String reason;
}
