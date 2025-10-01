package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
public class CreatePaymentResponse {
    private String paymentUrl;
}
