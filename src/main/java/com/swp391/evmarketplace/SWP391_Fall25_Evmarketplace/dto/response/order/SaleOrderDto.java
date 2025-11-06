package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleOrderDto {
    private Long id;

    private Long branchId;
    private String branchName;

    private Long listingId;
    private String listingTitle;

    private Long buyerId;
    private String buyerName;
    private String buyerPhone;

    private Long sellerId;
    private String sellerName;
    private String sellerPhone;

    private String orderNo;

    @JsonSerialize(using = ToStringSerializer.class)
    private BigInteger orderCode;

    private String contractUrl;
    private Long contractId;
    private String contractStatus;


    private Long consignmentAgreementId;

    private BigDecimal amount;
    private BigDecimal paidAmount;

    private OrderStatus status;

    private LocalDateTime reservedUntil;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Boolean isOpen;
    private Boolean isReservedActive;
    private Double paymentProcessPercent;

}
