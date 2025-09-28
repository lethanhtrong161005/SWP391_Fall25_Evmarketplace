package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.config.VNPayProperties;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.CreatePaymentBody;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.vnpay.VNPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private  VNPayProperties props;


    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(
            HttpServletRequest request,
            @RequestBody CreatePaymentBody body
    ) {
        try {
            if (body.getAmount() == null || body.getAmount() <= 0) {
                return ResponseEntity.badRequest().body("amount must be > 0 (VND)");
            }
            String ip = VNPayProperties.getIpAddress(request);
            String payUrl = vnPayService.createPaymentUrl(body.getAmount(), body.getDescription(), ip);
            return ResponseEntity.ok(payUrl);
        } catch (Exception e) {
            log.error("create-payment error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/vnpay/return")
    public ResponseEntity<Map<String, Object>> returnUrl(@RequestParam MultiValueMap<String, String> q) {
        Map<String, String> p = flatten(q);
        boolean valid = verifySignature(p);
        Map<String, Object> out = new HashMap<>();
        out.put("validSignature", valid);
        out.put("vnp_ResponseCode", p.get("vnp_ResponseCode"));
        out.put("vnp_TransactionStatus", p.get("vnp_TransactionStatus"));
        out.put("vnp_TxnRef", p.get("vnp_TxnRef"));
        out.put("raw", p);
        return ResponseEntity.ok(out);
    }

    @GetMapping("/vnpay/ipn")
    public ResponseEntity<String> ipn(@RequestParam MultiValueMap<String, String> q) {
        Map<String, String> p = flatten(q);
        log.info("[VNPay IPN] {}", p);

        if (!verifySignature(p)) {
            return ResponseEntity.ok("invalid-signature");
        }
        String resp = p.getOrDefault("vnp_ResponseCode", "");
        String trans = p.getOrDefault("vnp_TransactionStatus", "");
        // TODO: đối soát số tiền vnp_Amount/100 với DB + cập nhật trạng thái
        if ("00".equals(resp) && "00".equals(trans)) {
            return ResponseEntity.ok("ok");
        }
        return ResponseEntity.ok("failed");
    }

    private boolean verifySignature(Map<String, String> all) {
        String given = all.get("vnp_SecureHash");
        if (given == null || given.isBlank()) return false;
        Map<String,String> m = new HashMap<>(all);
        m.remove("vnp_SecureHash");
        m.remove("vnp_SecureHashType");
        List<String> keys = new ArrayList<>(m.keySet());
        Collections.sort(keys);
        StringBuilder data = new StringBuilder();
        for (String k : keys) {
            String v = m.get(k);
            if (v == null || v.isEmpty()) continue;
            if (data.length() > 0) data.append('&');
            data.append(k).append('=').append(v); // sử dụng bản đã-encode trong query string
        }
        String calc = props.hmacSHA512(props.getSecretKey(), data.toString());
        return calc.equalsIgnoreCase(given);
    }

    private static Map<String,String> flatten(MultiValueMap<String,String> mv) {
        Map<String,String> m = new HashMap<>();
        mv.forEach((k, v) -> m.put(k, (v != null && !v.isEmpty()) ? v.get(0) : ""));
        return m;
    }
}
