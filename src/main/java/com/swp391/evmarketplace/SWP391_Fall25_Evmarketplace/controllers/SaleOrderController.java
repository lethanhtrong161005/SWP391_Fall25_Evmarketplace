package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.order.CreateOrderBuyRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.SaleOrder;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.order.SaleOrderSerivce;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class SaleOrderController {
    @Autowired
    private SaleOrderSerivce  saleOrderSerivce;

    @PostMapping
    public ResponseEntity<?> createOrderByNow(@Valid @RequestBody CreateOrderBuyRequest req){
        var res = saleOrderSerivce.createOrder(req);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

}
