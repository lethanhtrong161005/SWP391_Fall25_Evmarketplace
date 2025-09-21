package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.ChangePasswordRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.RegisterAccountRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.RequestOtpDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.VerifyOtpDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.profile.UpdateProfileRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.AccountReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.LoginResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.OtpResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.profile.ProfileResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.mapper.AccountMapper;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.account.AccountService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.profile.ProfileService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Account APIs")
public class AccountController {
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ProfileService profileService;

    @GetMapping("/current")
    public ResponseEntity<?> getAccountDetails() {
        BaseResponse<AccountReponseDTO> response = new BaseResponse<>();
        Account ac = authUtil.getCurrentAccount();
        if (ac != null) {
            AccountReponseDTO accountReponseDTO = accountMapper.toAccountReponseDTO(ac);
            response.setData(accountReponseDTO);
            response.setMessage("Get Account Success");
            response.setSuccess(true);
            response.setStatus(200);
        } else {
            response.setMessage("Get Account Failed");
            response.setSuccess(false);
            response.setStatus(400);
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@Valid @RequestBody RequestOtpDTO requestOtpDTO) {
        BaseResponse<String> response = accountService.sendOtp(requestOtpDTO.getPhoneNumber());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpDTO dto) {
        BaseResponse<OtpResponse> response = accountService.verifyOtp(dto.getPhoneNumber(), dto.getOtp());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerAccount(@Valid @RequestBody RegisterAccountRequest request) {
        BaseResponse<LoginResponse> response = accountService.registerAccount(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        BaseResponse<Void> response = accountService.changePassword(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequestDTO requestDTO) {
        BaseResponse<ProfileResponseDTO> response = profileService.updateProfile(requestDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


}
