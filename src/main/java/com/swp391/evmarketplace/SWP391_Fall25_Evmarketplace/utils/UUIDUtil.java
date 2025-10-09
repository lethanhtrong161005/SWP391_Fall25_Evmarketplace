package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class UUIDUtil {

    public String generateDigits() {
        Random random = new Random();
        int number = 10000 + random.nextInt(90000);
        return String.valueOf(number);
    }

}
