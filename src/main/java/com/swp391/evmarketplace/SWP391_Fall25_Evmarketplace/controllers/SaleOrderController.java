package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.order.CreateOrderBuyRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.SaleOrder;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.OrderStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.order.SaleOrderSerivce;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/order")
public class SaleOrderController {
    @Autowired
    private SaleOrderSerivce  saleOrderSerivce;
    @Autowired
    private AuthUtil authUtil;

    @PostMapping
    public ResponseEntity<?> createOrderByNow(@Valid @RequestBody CreateOrderBuyRequest req){
        var res = saleOrderSerivce.createOrder(req);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping
    public ResponseEntity<?> getAllOrdersByUserId(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "desc") String dir,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
            ){
        var res = saleOrderSerivce.getAllOrdersByUserId(
                userId, orderNo, status, size, page, sort, orderNo, start, end
        );
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id){
        var res =  saleOrderSerivce.cancelOrder(id);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id){
        var res = saleOrderSerivce.getOrderDetails(id);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


}
