package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateSetScheduleRequestDTO {
    @NotNull
    private Long id;

    @NotNull
    @Future
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime appointmentTime;
}
