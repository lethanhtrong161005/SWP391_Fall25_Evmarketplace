package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.payment;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentBody {
    private PaymentMethod method;
    private Long amountVnd;
    private String referenceNo;
    private String note;
}
