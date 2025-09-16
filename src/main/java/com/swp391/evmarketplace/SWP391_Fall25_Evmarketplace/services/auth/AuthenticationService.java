package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.auth;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.GoogleCallbackRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.LoginRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.LoginResponse;

public interface AuthenticationService {
    BaseResponse<LoginResponse> loginWithPhoneNumber(LoginRequest request);
    BaseResponse<String> generateGoogleAuthUrl();
    BaseResponse<LoginResponse> googleLogin(GoogleCallbackRequest request);
}
