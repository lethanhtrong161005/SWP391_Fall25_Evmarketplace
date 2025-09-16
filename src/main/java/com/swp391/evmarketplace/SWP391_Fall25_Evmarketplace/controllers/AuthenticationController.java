package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.GoogleCallbackRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.LoginRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.LoginResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login-with-phone-number")
    public ResponseEntity<?> loginWithPhoneNumber(@RequestBody @Valid LoginRequest loginRequest) {
        BaseResponse<LoginResponse> baseResponse = authenticationService.loginWithPhoneNumber(loginRequest);
        return ResponseEntity.status(baseResponse.getStatus()).body(baseResponse);
    }

    @GetMapping("/google")
    public ResponseEntity<?> getGoogleUrl(){
        BaseResponse response = authenticationService.generateGoogleAuthUrl();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/google/callback")
    public ResponseEntity<?> callBack(@RequestBody @Valid GoogleCallbackRequest googleCallbackRequest) {
        BaseResponse response = authenticationService.googleLogin(googleCallbackRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
