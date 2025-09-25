package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class CustomBusinessException extends RuntimeException {
    private final int status;
    private final Map<String, List<String>> fieldErrors;

    public CustomBusinessException(String message) {
        super(message);
        this.status = 400;
        this.fieldErrors = null;
    }

    public CustomBusinessException(String message, int status) {
        super(message);
        this.status = status;
        this.fieldErrors = null;
    }

    public CustomBusinessException(String message, int status, Map<String, List<String>> fieldErrors) {
        super(message);
        this.status = status;
        this.fieldErrors = fieldErrors;
    }
}
