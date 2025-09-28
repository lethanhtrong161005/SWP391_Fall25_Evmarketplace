package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePaymentDto {

    @NotNull(message = "Total Price is required!")
    @Min(value = 0, message = "Total Price must not be negative!")
    private Float totalPrice;

}
