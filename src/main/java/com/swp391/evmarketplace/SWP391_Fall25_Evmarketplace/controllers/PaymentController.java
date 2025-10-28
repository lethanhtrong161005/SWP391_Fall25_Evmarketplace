package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.payment.CreatePaymentBody;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.PaymentMethod;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing.ListingService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.payment.PaymentService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.vnpay.VNPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/promotion/{listingId}")
    public ResponseEntity<BaseResponse<String>> createPromotion(@PathVariable Long listingId,
                                                                HttpServletRequest request) {
        var res = paymentService.createPromotionPaymentUrl(listingId, request);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<?> createPayment(@PathVariable Long orderId,
                                           @RequestBody(required = false) CreatePaymentBody body,
                                           HttpServletRequest request) {
        var method = (body == null || body.getMethod() == null) ? PaymentMethod.VNPAY : body.getMethod();
        BaseResponse<?> res;
        if (method == PaymentMethod.VNPAY) {
            Long amount = (body != null ? body.getAmountVnd() : null);
            res = paymentService.createOrderPaymentUrl(orderId, amount, request);
        }else if (method == PaymentMethod.CASH) {
            Long amount = (body != null ? body.getAmountVnd() : null);
            res = paymentService.recordCashPayment(
                    orderId, amount,
                    body != null ? body.getReferenceNo() : null,
                    body != null ? body.getNote() : null
            );
        } else {
            throw new CustomBusinessException("Method not supported");
        }
        return ResponseEntity.status(res.getStatus()).body(res);
    }


    @GetMapping("/vnpay/return")
    public ResponseEntity<BaseResponse<Map<String,Object>>> vnpReturn(
            @RequestParam MultiValueMap<String,String> q) {
        var res = paymentService.handleVnpReturn(q);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/vnpay/ipn")
    public ResponseEntity<String> vnpIpn(@RequestParam MultiValueMap<String,String> q) {
        return ResponseEntity.ok(paymentService.handleVnpIpn(q));
    }


    @GetMapping
    public ResponseEntity<?> getPaymentsByOrderId(
            @RequestParam Long orderId,
            @RequestParam(required = false) Long lastId, // ID cuối cùng của batch trước
            @RequestParam(defaultValue = "4") int limit
    ) {
        var res = paymentService.getPaymentsByOrderId(orderId, lastId, limit);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

}
