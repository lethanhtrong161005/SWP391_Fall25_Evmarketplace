package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.validation;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.MobileNetwork;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) return false;

        if (phone.length() != 10) return false;

        if (!phone.matches("\\d{10}")) return false;

        return MobileNetwork.fromPhoneNumber(phone) != null;
    }

}
