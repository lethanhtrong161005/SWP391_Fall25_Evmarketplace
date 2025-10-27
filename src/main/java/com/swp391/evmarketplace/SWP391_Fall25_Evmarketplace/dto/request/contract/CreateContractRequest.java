package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.contract;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateContractRequest {

    @NotNull
    private Long orderId;

    @NotNull
    private Long staffId;

    private String note;

    @NotNull
    private LocalDateTime effectiveFrom;

    @NotNull
    private LocalDateTime effectiveTo;
}
