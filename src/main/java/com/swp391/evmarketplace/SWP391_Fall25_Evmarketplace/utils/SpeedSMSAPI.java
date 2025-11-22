package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class SpeedSMSAPI {

    @Value("${smsSpeed.apiUrl}")
    private String API_URL;

    @Value("${smsSpeed.token}")
    private String mAccessToken;

    @Value("${smsSpeed.sender}")
    private String sender;

    @Value("${smsSpeed.type}")
    private int type;

    /**
     * Get user information
     * @param: none
     * @return: json string
     * */
    public String getUserInfo() throws IOException {
        URL url = new URL(API_URL + "/user/info");
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        String userCredentials = mAccessToken + ":x";
        String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userCredentials.getBytes());
        conn.setRequestProperty ("Authorization", basicAuth);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine = "";
        StringBuffer buffer = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            buffer.append(inputLine);
        }
        in.close();
        return buffer.toString();
    }

    /**
     * Send SMS
     * @param
     * @return: json string
     * */
    public String sendSMS(String to, String content) throws IOException {
        to = normalizePhoneNumber(to);
        System.out.println("PHONE NUMBER: " + to);
        String json = "{\"to\": [\"" + to + "\"], \"content\": \"" + EncodeNonAsciiCharacters(content) + "\", \"type\":" + type + ", \"brandname\":\"" + sender + "\"}";
        URL url = new URL(API_URL + "/sms/send");
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        String userCredentials = mAccessToken + ":x";
        String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userCredentials.getBytes());
        conn.setRequestProperty ("Authorization", basicAuth);
        conn.setRequestProperty("Content-Type", "application/json");

        conn.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(json);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine = "";
        StringBuffer buffer = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            buffer.append(inputLine);
        }
        in.close();
        return buffer.toString();
    }


    private String EncodeNonAsciiCharacters(String value) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            int unit = (int)c;
            if (unit > 127) {
                String hex = String.format("%04x", (int)unit);
                String encodedValue = "\\u" + hex;
                sb.append(encodedValue);
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
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
