package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public final class VNPayUtil {
    private VNPayUtil(){}

    public static String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(raw.length * 2);
            for (byte b : raw) sb.append(String.format("%02x", b & 0xff));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC error", e);
        }
    }

    public static String urlEncode(String s) {
        try { return URLEncoder.encode(s, StandardCharsets.UTF_8.toString()); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public static String buildQueryAndData(Map<String, String> params, StringBuilder dataOut) {
        Map<String, String> copy = new HashMap<>(params);
        copy.remove("vnp_SecureHash");
        copy.remove("vnp_SecureHashType");

        List<String> keys = new ArrayList<>(copy.keySet());
        Collections.sort(keys);

        StringBuilder query = new StringBuilder();
        StringBuilder data  = new StringBuilder();
        for (String k : keys) {
            String v = copy.get(k);
            if (v == null || v.isEmpty()) continue;
            String enc = URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8);
            query.append(k).append("=").append(enc).append("&");
            data.append(k).append("=").append(enc).append("&");
        }
        if (query.length() > 0) query.setLength(query.length() - 1);
        if (data.length()  > 0) data.setLength(data.length() - 1);

        if (dataOut != null) dataOut.append(data);
        return query.toString();
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("Etc/GMT+7"));
        return sdf.format(date);
    }
}
