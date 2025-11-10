package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.account;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.account.RegisterAccountRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.account.UpdateEmailRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.auth.ChangePasswordRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.auth.ResetPasswordRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.auth.VerifyEmailOtpRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.account.AccountReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.auth.GoogleUserInfoDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.account.CreateStaffAccountRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.auth.LoginResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.auth.OtpResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.StaffAccountResponseDTO;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountRole;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountStatus;

import java.util.List;
import java.util.Map;

public interface AccountService {
    Account upsertUser(GoogleUserInfoDTO userInfo);
    BaseResponse<String> sendOtpRegister(String phoneNumber);
    BaseResponse<String> sendOtpReset(String phoneNumber);
    BaseResponse<OtpResponse> verifyOtp(String phoneNumber, String otp);
    BaseResponse<LoginResponse> registerAccount(RegisterAccountRequest request);
    BaseResponse<Void> changePassword(ChangePasswordRequest request);
    BaseResponse<Void> updateEmail(UpdateEmailRequestDTO requestDTO);

    BaseResponse<?> getAccountCurrent();

    BaseResponse<Void> sendOtpEmail(String email);

    //admin
    BaseResponse<Map<String, Object>> getAll(int page, int size, String sort, String dir);
    BaseResponse<Map<String, Object>> search(String keyword, int page, int size, String sort, String dir);
    BaseResponse<AccountReponseDTO> getAccountById(Long id);
    BaseResponse<Void> blockAccount(Long accountId);
    BaseResponse<Void> unblockAccount(Long accountId);
    BaseResponse<StaffAccountResponseDTO> createStaffAccount(CreateStaffAccountRequestDTO requestDTO);
    BaseResponse<Void> resetPassword(ResetPasswordRequest request);
    BaseResponse<?> getStaffListInBranch();

}
