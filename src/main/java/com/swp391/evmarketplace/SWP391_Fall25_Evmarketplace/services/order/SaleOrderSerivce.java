package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.order;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.order.CreateOrderBuyRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;

public interface SaleOrderSerivce {
    BaseResponse<?> createOrder(CreateOrderBuyRequest req);
}
