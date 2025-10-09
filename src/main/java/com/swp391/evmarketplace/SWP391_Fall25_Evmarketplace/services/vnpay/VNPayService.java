package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.vnpay;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.config.VNPayProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

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

    public String createPaymentUrl(
            long amountVnd,
            String description,
            String ipAddress,
            String txnRef,
            Map<String,String> returnParams,
            boolean forceNCB
    ) {
        String vnpAmount = BigDecimal.valueOf(amountVnd).setScale(0, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).toPlainString();
        ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime exp = now.plusMinutes(20);

        Map<String, String> vnp = new HashMap<>();
        vnp.put("vnp_Version",   cfg.getVnp_Version());
        vnp.put("vnp_Command",   cfg.getVnp_Command());
        vnp.put("vnp_TmnCode",   cfg.getVnp_TmnCode());
        vnp.put("vnp_Amount",    vnpAmount);
        vnp.put("vnp_CurrCode",  "VND");
        vnp.put("vnp_TxnRef",    txnRef);
        vnp.put("vnp_OrderInfo", (description == null || description.isBlank())
                ? ("Thanh toan " + txnRef) : description);
        vnp.put("vnp_OrderType", "other");
        vnp.put("vnp_Locale",    "vn");
        if (forceNCB) vnp.put("vnp_BankCode",  "NCB");
        vnp.put("vnp_IpAddr",    ipAddress);
        vnp.put("vnp_ReturnUrl", buildReturnUrl(cfg.getVnp_ReturnUrl(), returnParams));
        vnp.put("vnp_CreateDate", now.format(VNP_DTF));
        vnp.put("vnp_ExpireDate", exp.format(VNP_DTF));

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
        return cfg.getVnp_PayUrl() + "?" + query
                + "&vnp_SecureHashType=HmacSHA512"
                + "&vnp_SecureHash=" + secure;
    }

    /** Dùng cho controller/service: không decode lại vì framework đã decode sẵn. */
    public Map<String,String> flatten(MultiValueMap<String,String> mv) {
        Map<String,String> m = new HashMap<>();
        mv.forEach((k, v) -> m.put(k, (v != null && !v.isEmpty()) ? v.get(0) : ""));
        return m;
    }

    /** Verify theo chuẩn VNPay: chỉ tham số bắt đầu bằng vnp_, đã URL-encode lại trước khi HMAC. */
    public boolean verifySignature(Map<String, String> all) {
        String given = all.get("vnp_SecureHash");
        if (given == null || given.isBlank()) return false;

        List<String> keys = all.keySet().stream()
                .filter(k -> k.startsWith("vnp_"))
                .filter(k -> !k.equals("vnp_SecureHash") && !k.equals("vnp_SecureHashType"))
                .sorted()
                .toList();

        StringBuilder data = new StringBuilder();
        for (String k : keys) {
            String v = all.get(k);
            if (v == null || v.isEmpty()) continue;
            if (data.length() > 0) data.append('&');
            String enc = URLEncoder.encode(v, StandardCharsets.UTF_8);
            data.append(k).append('=').append(enc);
        }

        String calc = cfg.hmacSHA512(cfg.getSecretKey(), data.toString());
        return calc.equalsIgnoreCase(given);
    }

    public long parseAmountVnd(String vnpAmount) {
        try { return (vnpAmount == null || vnpAmount.isBlank()) ? 0L : Long.parseLong(vnpAmount) / 100L; }
        catch (Exception e) { return 0L; }
    }

    private String buildReturnUrl(String base, Map<String,String> params) {
        if (params == null || params.isEmpty()) return base;
        StringBuilder sb = new StringBuilder(base);
        sb.append(base.contains("?") ? "&" : "?");
        boolean first = true;
        for (var e : params.entrySet()) {
            if (!first) sb.append('&'); first = false;
            sb.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }
}
