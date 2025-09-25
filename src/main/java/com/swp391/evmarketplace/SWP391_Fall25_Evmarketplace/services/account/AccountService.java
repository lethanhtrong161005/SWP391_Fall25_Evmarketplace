package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.account;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.GoogleUserInfoDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.ChangePasswordRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.RegisterAccountRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.ResetPasswordRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.LoginResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.OtpResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;

public interface AccountService {
    Account upsertUser(GoogleUserInfoDTO userInfo);
    BaseResponse<String> sendOtpRegister(String phoneNumber);
    BaseResponse<String> sendOtpReset(String phoneNumber);
    BaseResponse<OtpResponse> verifyOtp(String phoneNumber, String otp);
    BaseResponse<LoginResponse> registerAccount(RegisterAccountRequest request);
    BaseResponse<Void> changePassword(ChangePasswordRequest request);
    BaseResponse<Void> resetPassword(ResetPasswordRequest request);
}
