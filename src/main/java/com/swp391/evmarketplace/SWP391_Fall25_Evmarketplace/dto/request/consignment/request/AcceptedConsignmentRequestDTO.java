package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcceptedConsignmentRequestDTO {
    @NotNull
    Long id;
}
