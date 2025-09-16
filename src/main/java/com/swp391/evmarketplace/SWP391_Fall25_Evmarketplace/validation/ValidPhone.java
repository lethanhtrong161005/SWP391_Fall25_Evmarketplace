package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {
    String message() default "Invalid phone number format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
