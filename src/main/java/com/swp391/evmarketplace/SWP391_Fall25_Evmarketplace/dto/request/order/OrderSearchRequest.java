package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.order;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderSearchRequest {
    private String orderNo;
    private OrderStatus status;
    private Integer size;
    private Integer page;
    private String sort;
    private String dir;
    private LocalDateTime start;
    private LocalDateTime end;
}
