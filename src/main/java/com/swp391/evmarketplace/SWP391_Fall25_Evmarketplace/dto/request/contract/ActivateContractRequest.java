package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.contract;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateContractRequest {

    @NotNull(message = "Contract id is required")
    private Long contractId;

    @NotNull(message = "Buyer id is required")
    private Long buyerId;

}
