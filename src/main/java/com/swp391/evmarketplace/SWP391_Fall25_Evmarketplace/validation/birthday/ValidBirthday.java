package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.validation.birthday;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BirthdayValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBirthday {
    String message() default "birthday must be a valid past date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String pattern() default "yyyy-MM-dd";

    boolean past() default false;

    int minAge() default 0;

    int maxAge() default 0;
}

