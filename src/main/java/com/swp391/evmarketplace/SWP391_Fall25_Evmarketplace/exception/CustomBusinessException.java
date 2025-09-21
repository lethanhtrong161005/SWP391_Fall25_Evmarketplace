package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ErrorCode;
import lombok.Data;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class CustomBusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final int status;
    private final Map<String, List<String>> fieldErrors;

    public CustomBusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessageEn());
        this.errorCode = errorCode;
        this.status = errorCode.getHttpStatus().value();
        this.fieldErrors = null;
    }

    public String getCode() {
        return errorCode.getCode();
    }


    public CustomBusinessException(String message) {
        super(message);
        this.errorCode = null;
        this.status = 400;
        this.fieldErrors = null;
    }

    public CustomBusinessException(String message, ErrorCode errorCode, int status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.fieldErrors = null;
    }

    public CustomBusinessException(String message, ErrorCode errorCode, int status, Map<String, List<String>> fieldErrors) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.fieldErrors = fieldErrors;
    }
}
