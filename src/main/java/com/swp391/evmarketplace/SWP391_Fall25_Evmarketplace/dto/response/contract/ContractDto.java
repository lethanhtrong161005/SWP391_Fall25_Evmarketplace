package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.contract;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.order.SaleOrderDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ContractSignMethod;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ContractStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContractDto {
    private Long id;
    private String fileUrl;
    private Long orderId;
    private ContractSignMethod signMethod;
    private ContractStatus status;
    private LocalDateTime signAt;
    private LocalDateTime updateAt;
    private LocalDateTime createAt;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
}
