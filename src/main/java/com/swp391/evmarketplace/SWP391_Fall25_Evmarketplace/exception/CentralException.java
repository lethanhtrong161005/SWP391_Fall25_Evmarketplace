package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.BaseResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class CentralException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        for(FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), List.of(fieldError.getDefaultMessage()));
        }
        BaseResponse<Void> response = new BaseResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                false,
                "Validation failed",
                null,
                errors,
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity<BaseResponse<Void>> handleCustomBusinessException(CustomBusinessException ex) {
        BaseResponse<Void> response = new BaseResponse<>(
                ex.getStatus(),
                false,
                ex.getCode(),
                null,
                ex.getFieldErrors(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<BaseResponse<Void>> handleExpiredJwtException(ExpiredJwtException ex) {
        BaseResponse<Void> response = new BaseResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                false,
                "JWT token is expired",
                null,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGenericException(Exception ex) {
        BaseResponse<Void> response = new BaseResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                false,
                "Internal Server Error: " + ex.getMessage(),
                null,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {
        BaseResponse<String> response = new BaseResponse<>(
                HttpStatus.FORBIDDEN.value(),
                false,
                "Access Denied",
                "You do not have permission to access this resource",
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
