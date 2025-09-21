package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // ===== AUTH =====
    AUTH_INVALID_CREDENTIALS("AUTH.INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, "Invalid credentials"),
    AUTH_UNAUTHORIZED("AUTH.UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "Authentication required"),
    AUTH_ACCESS_DENIED("AUTH.ACCESS_DENIED", HttpStatus.FORBIDDEN, "Access denied"),
    AUTH_TOKEN_INVALID("AUTH.TOKEN_INVALID", HttpStatus.UNAUTHORIZED, "Token is invalid"),
    AUTH_TOKEN_EXPIRED("AUTH.TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED, "Token is expired"),

    // ===== ACCOUNT / PROFILE =====
    ACCOUNT_NOT_FOUND("ACCOUNT.NOT_FOUND", HttpStatus.NOT_FOUND, "Account not found"),
    ACCOUNT_ALREADY_EXISTS("ACCOUNT.ALREADY_EXISTS", HttpStatus.CONFLICT, "Account already exists"),
    ACCOUNT_EMAIL_EXISTS("ACCOUNT.EMAIL_EXISTS", HttpStatus.CONFLICT, "Email already exists"),
    ACCOUNT_PHONE_EXISTS("ACCOUNT.PHONE_EXISTS", HttpStatus.CONFLICT, "Phone number already exists"),
    ACCOUNT_PHONE_NOT_VERIFIED("ACCOUNT.PHONE_NOT_VERIFIED", HttpStatus.FORBIDDEN, "Phone number is not verified"),
    PROFILE_NOT_FOUND("PROFILE.NOT_FOUND", HttpStatus.NOT_FOUND, "Profile not found"),

    // ===== LISTING / PRODUCT =====
    LISTING_NOT_FOUND("LISTING.NOT_FOUND", HttpStatus.NOT_FOUND, "Listing not found"),
    LISTING_ALREADY_SOLD("LISTING.ALREADY_SOLD", HttpStatus.CONFLICT, "Listing already sold"),
    LISTING_INVALID_PRICE("LISTING.INVALID_PRICE", HttpStatus.BAD_REQUEST, "Invalid listing price"),
    LISTING_QUOTA_EXCEEDED("LISTING.QUOTA_EXCEEDED", HttpStatus.FORBIDDEN, "Listing quota exceeded"),
    LISTING_DUPLICATE_VIN("LISTING.DUPLICATE_VIN", HttpStatus.CONFLICT, "VIN already exists"),

    // ===== BATTERY / INVENTORY =====
    BATTERY_NOT_FOUND("BATTERY.NOT_FOUND", HttpStatus.NOT_FOUND, "Battery not found"),
    BATTERY_CHEMISTRY_UNSUPPORTED("BATTERY.CHEMISTRY_UNSUPPORTED", HttpStatus.BAD_REQUEST, "Unsupported battery chemistry"),
    INVENTORY_NOT_ENOUGH("INVENTORY.NOT_ENOUGH", HttpStatus.CONFLICT, "Not enough inventory"),

    // ===== ORDER / PAYMENT / ESCROW =====
    ORDER_NOT_FOUND("ORDER.NOT_FOUND", HttpStatus.NOT_FOUND, "Order not found"),
    ORDER_STATUS_INVALID("ORDER.STATUS_INVALID", HttpStatus.BAD_REQUEST, "Order status invalid"),
    PAYMENT_FAILED("PAYMENT.FAILED", HttpStatus.BAD_REQUEST, "Payment failed"),
    PAYMENT_INSUFFICIENT_BALANCE("PAYMENT.INSUFFICIENT_BALANCE", HttpStatus.BAD_REQUEST, "Insufficient balance"),
    PAYMENT_METHOD_UNSUPPORTED("PAYMENT.METHOD_UNSUPPORTED", HttpStatus.BAD_REQUEST, "Payment method unsupported"),
    ESCROW_REQUIRED("ESCROW.REQUIRED", HttpStatus.FORBIDDEN, "Escrow is required"),
    ESCROW_ENABLE_REQUIRES_SUBSCRIPTION("ESCROW.ENABLE_REQUIRES_SUBSCRIPTION", HttpStatus.FORBIDDEN, "Escrow feature requires subscription"),

    // ===== VALIDATION (generic) =====
    VALIDATION_ERROR("VALIDATION.ERROR", HttpStatus.BAD_REQUEST, "Validation failed"),
    VALIDATION_FIELD_REQUIRED("VALIDATION.FIELD_REQUIRED", HttpStatus.BAD_REQUEST, "Field is required"),
    VALIDATION_INVALID_FORMAT("VALIDATION.INVALID_FORMAT", HttpStatus.BAD_REQUEST, "Invalid format"),
    VALIDATION_TOO_SHORT("VALIDATION.TOO_SHORT", HttpStatus.BAD_REQUEST, "Value is too short"),
    VALIDATION_TOO_LONG("VALIDATION.TOO_LONG", HttpStatus.BAD_REQUEST, "Value is too long"),
    VALIDATION_OUT_OF_RANGE("VALIDATION.OUT_OF_RANGE", HttpStatus.BAD_REQUEST, "Value is out of range"),
    VALIDATION_DATE_INVALID("VALIDATION.DATE_INVALID", HttpStatus.BAD_REQUEST, "Date is invalid"),
    VALIDATION_PAST_DATE("VALIDATION.PAST_DATE", HttpStatus.BAD_REQUEST, "Date must be in the past"),
    VALIDATION_DUPLICATE("VALIDATION.DUPLICATE", HttpStatus.BAD_REQUEST, "Duplicate value"),

    // ===== SYSTEM =====
    SERVICE_UNAVAILABLE("SYSTEM.SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE, "Service unavailable"),
    DATABASE_ERROR("SYSTEM.DATABASE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "Database error"),
    INTERNAL_SERVER_ERROR("SYSTEM.INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    UNKNOWN_ERROR("SYSTEM.UNKNOWN_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error");


    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessageEn;

    ErrorCode(String code, HttpStatus httpStatus, String defaultMessageEn) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.defaultMessageEn = defaultMessageEn;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getDefaultMessageEn() {
        return defaultMessageEn;
    }
}
