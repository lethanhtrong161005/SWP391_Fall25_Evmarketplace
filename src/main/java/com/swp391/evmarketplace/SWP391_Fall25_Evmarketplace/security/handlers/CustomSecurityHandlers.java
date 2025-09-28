package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomSecurityHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        writeErrorResponse(response, HttpStatus.UNAUTHORIZED, authException.getMessage());
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        writeErrorResponse(response, HttpStatus.FORBIDDEN, accessDeniedException.getMessage());
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        BaseResponse<Void> res = new BaseResponse<>(
                status.value(),
                false,
                message,
                null,
                null,
                LocalDateTime.now()
        );

        response.setContentType("application/json");
        response.setStatus(status.value());
        objectMapper.writeValue(response.getWriter(), res);
    }
}
