package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.payment;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.PaymentMethod;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.PaymentPurpose;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalePaymentDto {

    private Long id;

    private Long orderId;
    private String orderNo;

    private Long listingId;
    private String listingTitle;

    private Long payerId;
    private String payerName;
    private String payerPhone;

    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentPurpose purpose;
    private PaymentStatus status;

    private String providerTxnId;

    private String referenceNo;
    private Long recordedBy;

    private String note;
    private LocalDateTime paidAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

}
