package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.config.VNPayProperties;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.CreatePaymentBody;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
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

}
