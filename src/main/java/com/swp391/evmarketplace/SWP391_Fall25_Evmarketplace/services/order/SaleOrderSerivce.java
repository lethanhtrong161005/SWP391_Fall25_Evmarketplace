package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.order;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.order.CreateOrderBuyRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.OrderStatus;

import java.time.LocalDateTime;

public interface SaleOrderSerivce {
    BaseResponse<?> createOrder(CreateOrderBuyRequest req);


    BaseResponse<?> getAllOrdersByStaffId(
            Long staffId,
            String orderNo,
            OrderStatus status,
            int size,
            int page,
            String sort,
            String dir,
            LocalDateTime start,
            LocalDateTime end
    );


}
