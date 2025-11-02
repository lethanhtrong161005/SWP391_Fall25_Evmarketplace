package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.contract;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ContractStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateContractRequest {
    private ContractStatus status;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private String note;
}
