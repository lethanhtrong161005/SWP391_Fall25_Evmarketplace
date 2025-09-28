package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentBody {
    private Long amount;
    private String description;
}
