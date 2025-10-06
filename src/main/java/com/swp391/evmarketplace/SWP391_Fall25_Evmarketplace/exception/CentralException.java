package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.nio.file.NoSuchFileException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CentralException {

    private <T> ResponseEntity<BaseResponse<T>> build(int status,
                                                      boolean success,
                                                      String message,
                                                      T data,
                                                      Map<String, List<String>> fieldErrors) {
        BaseResponse<T> body = new BaseResponse<>(
                status,
                success,
                message,
                data,
                fieldErrors,
                LocalDateTime.now()
        );
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)   // quan trọng: luôn ép JSON
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(err -> Optional.ofNullable(err.getDefaultMessage()).orElse(""), Collectors.toList())
                ));
        return build(HttpStatus.BAD_REQUEST.value(), false, "Validation failed", null, errors);
    }

    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity<BaseResponse<Void>> handleCustomBusinessException(CustomBusinessException ex) {
        return build(ex.getStatus(), false, ex.getMessage(), null, ex.getFieldErrors());
    }

    @ExceptionHandler(NoSuchFileException.class)
    public ResponseEntity<BaseResponse<Void>> handleNoSuchFile(NoSuchFileException ex) {
        // Khi file không tồn tại (download ảnh/video)
        return build(HttpStatus.NOT_FOUND.value(), false, "File not found", null, null);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<BaseResponse<Void>> handleExpiredJwtException(ExpiredJwtException ex) {
        return build(HttpStatus.UNAUTHORIZED.value(), false, "JWT token is expired", null, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN.value(), false, "Access Denied", null, null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<BaseResponse<Void>> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        // Điều chỉnh message theo limit thật trong cấu hình của bạn
        return build(413, false, "File too large! Maximum allowed size is 500MB.", null, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGenericException(Exception ex) {
        // Tránh lộ chi tiết nội bộ; log stacktrace ở layer logging thay vì đưa ra client
        ex.printStackTrace();
        return build(HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "Internal Server Error", null, null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Void>> handleBadJson(HttpMessageNotReadableException ex) {
        BaseResponse<Void> res = new BaseResponse<>();
        res.setSuccess(false);
        res.setStatus(400);
        res.setMessage("Bad JSON: " + ex.getMostSpecificCause().getMessage());
        return ResponseEntity.badRequest().body(res);
    }


}
