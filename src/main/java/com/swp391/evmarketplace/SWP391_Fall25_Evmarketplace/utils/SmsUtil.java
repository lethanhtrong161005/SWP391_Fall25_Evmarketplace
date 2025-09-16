package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class SmsUtil {

    @Value("${mocean.endpoint}")
    private String MOCE_API_URL;

    @Value("${mocean.token}")
    private String BEARER_TOKEN;

    public boolean sendOtpSms(String phone, String otp) {
        try {
            phone = normalizePhoneNumber(phone);
            RestTemplate restTemplate = new RestTemplate();

            String text = "Your OTP is: " + otp;
            String body = "mocean-to=" + phone +
                    "&mocean-from=EvMarket" +
                    "&mocean-text=" + text;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBearerAuth(BEARER_TOKEN);

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    MOCE_API_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            return response.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            throw new CustomBusinessException("Failed to send OTP: " + e.getMessage());
        }
    }

    private String normalizePhoneNumber(String phone){
        if (phone == null || phone.isBlank()) {
            throw new CustomBusinessException("Phone number cannot be empty");
        }

        phone = phone.trim();

        if (phone.startsWith("0")) {
            return "84" + phone.substring(1);
        } else if (phone.startsWith("+84")) {
            return phone.substring(1);
        } else {
            return phone;
        }

    }
}
