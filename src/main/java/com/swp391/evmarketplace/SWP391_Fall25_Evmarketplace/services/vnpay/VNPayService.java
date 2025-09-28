package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.vnpay;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.config.VNPayProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VNPayService {
    private final VNPayProperties cfg;
    private static final DateTimeFormatter VNP_DTF = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public String createPaymentUrl(long amountVnd, String description, String ipAddress) {
        // 1) Amount nguyên VND ×100 (chuỗi số nguyên, không .0, không E+)
        BigDecimal vnd = BigDecimal.valueOf(amountVnd).setScale(0, RoundingMode.HALF_UP);
        String vnpAmount = vnd.multiply(BigDecimal.valueOf(100)).toPlainString();

        // 2) Thời gian GMT+7
        ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime exp = now.plusMinutes(20); // có thể đưa vào cấu hình nếu muốn

        // 3) TxnRef A-Z0-9 (tránh ký tự lạ)
        String txnRef = "ORD" + System.currentTimeMillis();

        // 4) Params
        Map<String, String> vnp = new HashMap<>();
        vnp.put("vnp_Version",   cfg.getVnp_Version());
        vnp.put("vnp_Command",   cfg.getVnp_Command());
        vnp.put("vnp_TmnCode",   cfg.getVnp_TmnCode());
        vnp.put("vnp_Amount",    vnpAmount);
        vnp.put("vnp_CurrCode",  "VND");
        vnp.put("vnp_TxnRef",    txnRef);
        vnp.put("vnp_OrderInfo", (description == null || description.isBlank())
                ? ("Thanh toan don " + txnRef)
                : description);
        vnp.put("vnp_OrderType", "other");
        vnp.put("vnp_Locale",    "vn");
        vnp.put("vnp_BankCode",  "VNBANK"); // sandbox: VNBANK/NCB đều ok
        vnp.put("vnp_IpAddr",    ipAddress);
        vnp.put("vnp_ReturnUrl", cfg.getVnp_ReturnUrl().trim());
        vnp.put("vnp_CreateDate", now.format(VNP_DTF));
        vnp.put("vnp_ExpireDate", exp.format(VNP_DTF));

        // 5) Build query & data (UTF-8, encode 1 lần, sort key)
        List<String> keys = new ArrayList<>(vnp.keySet());
        Collections.sort(keys);

        StringBuilder data = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String k : keys) {
            String val = vnp.get(k);
            if (val == null || val.isEmpty()) continue;
            String enc = URLEncoder.encode(val, StandardCharsets.UTF_8);
            if (data.length() > 0) { data.append('&'); query.append('&'); }
            data.append(k).append('=').append(enc);
            query.append(k).append('=').append(enc);
        }

        String secure = cfg.hmacSHA512(cfg.getSecretKey(), data.toString());
        String url = cfg.getVnp_PayUrl()
                + "?" + query
                + "&vnp_SecureHashType=HmacSHA512"
                + "&vnp_SecureHash=" + secure;

        // 6) Tự-verify lại chính URL vừa build (bắt mọi lệch param/encode)
        Map<String,String> sent = new HashMap<>();
        String qs = url.substring(url.indexOf('?') + 1);
        for (String part : qs.split("&")) {
            int i = part.indexOf('=');
            if (i > 0) sent.put(part.substring(0, i), part.substring(i + 1));
        }
        String given = sent.remove("vnp_SecureHash");
        sent.remove("vnp_SecureHashType");
        StringBuilder data2 = new StringBuilder();
        // build lại bằng cùng logic
        List<String> k2 = new ArrayList<>(sent.keySet());
        Collections.sort(k2);
        for (String k : k2) {
            String v = sent.get(k);
            if (v == null || v.isEmpty()) continue;
            if (data2.length() > 0) data2.append('&');
            data2.append(k).append('=').append(v);
        }
        String calc2 = cfg.hmacSHA512(cfg.getSecretKey(), data2.toString());
        boolean selfOk = calc2.equalsIgnoreCase(given);
        log.info("[VNPAY] selfVerify={}, tmn={}, secretLen={}, create={}, expire={}",
                selfOk, cfg.getVnp_TmnCode(), cfg.getSecretKey().trim().length(),
                vnp.get("vnp_CreateDate"), vnp.get("vnp_ExpireDate"));

        return url;
    }
}
