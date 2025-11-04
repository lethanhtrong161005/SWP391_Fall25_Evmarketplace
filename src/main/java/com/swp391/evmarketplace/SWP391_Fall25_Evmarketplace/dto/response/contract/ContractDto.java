package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractDto {
    private Long id;

    private Long orderId;
    private String orderNo;
    private String orderCode;

    private String listingTitle;

    private String buyerName;
    private String buyerPhoneNumber;

    private String sellerName;
    private String branchName;

    private ContractSignMethod signMethod;
    private ContractStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime signedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime effectiveFrom;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime effectiveTo;

    private String fileUrl;

}
