package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.order;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderBuyRequest {

    @NotNull(message = "Listing ID is required")
    private Long listingId;

    @NotNull(message = "Buyer ID is required")
    private Long buyerId;

}
