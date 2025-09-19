package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.validation.birthday;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Annotation;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class BirthdayValidator implements ConstraintValidator<ValidBirthday, String> {

    private String pattern;
    private boolean past;
    private int minAge;
    private int maxAge;


    @Override
    public void initialize(ValidBirthday constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
        this.past = constraintAnnotation.past();
        this.minAge = constraintAnnotation.minAge();
        this.maxAge = constraintAnnotation.maxAge();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate date;

        try {
            date = LocalDate.parse(s, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeException e) {
            return false;
        }

        LocalDate today = LocalDate.now();

        if (past && !date.isBefore(today)) {
            return false;
        }

        if (minAge > 0) {
            int age = Period.between(date, today).getYears();
            if(age < minAge) return false;
        }

        if(maxAge > 0 ){
            int age = Period.between(date, today).getYears();
            if(age > maxAge) return false;
        }
        return true;
    }
}
