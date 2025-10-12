package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.account.UpdateEmailRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.auth.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.account.RegisterAccountRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.profile.UpdateProfileRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.account.AccountReponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.auth.LoginResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.auth.OtpResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.profile.ProfileResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.OtpType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.mapper.AccountMapper;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ProfileRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.account.AccountService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.listing.ListingService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.profile.ProfileService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private FileService fileService;
    @Value("${server.url}")
    private String serverUrl;
    @Autowired
    private ListingService listingService;


    @GetMapping("/current")
    public ResponseEntity<?> getAccountDetails() {
        BaseResponse<AccountReponseDTO> response = new BaseResponse<>();
        Account ac = authUtil.getCurrentAccount();
        if (ac != null) {
            AccountReponseDTO accountReponseDTO = accountMapper.toAccountReponseDTO(ac);
            String avatarUrl = serverUrl + "/api/files/images/" + accountReponseDTO.getProfile().getAvatarUrl();
            if (accountReponseDTO.getProfile().getAvatarUrl() != null) {
                if(ac.getGoogleId() == null) {
                    accountReponseDTO.getProfile().setAvatarUrl(avatarUrl);
                }
            }
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
    public ResponseEntity<BaseResponse<String>> requestOtp(@Valid @RequestBody RequestOtpDTO dto) {
        BaseResponse<String> response;
        if (dto.getType() == OtpType.REGISTER) {
            response = accountService.sendOtpRegister(dto.getPhoneNumber());
        } else if (dto.getType() == OtpType.RESET) {
            response = accountService.sendOtpReset(dto.getPhoneNumber());
        } else {
            throw new CustomBusinessException("Unsupported OTP type");
        }
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

    @PutMapping(value = "/update-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<String>> updateAvatar(@RequestParam("file") MultipartFile file) {
        BaseResponse<String> response = profileService.updateAvatar(file);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //update email
    @PostMapping("/email/request-otp")
    public ResponseEntity<BaseResponse<String>> requestEmailOtp(@Valid @RequestBody EmailOtpRequestDTO dto) {
        BaseResponse<String> response = accountService.sendOtpEmail(dto.getEmail());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/email/verify-otp")
    public ResponseEntity<?> verifyEmailOtp(@Valid @RequestBody VerifyEmailOtpRequestDTO dto) {
        BaseResponse<OtpResponse> response = accountService.verifyEmailOtp(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/update-email")
    public ResponseEntity<?> updateEmail(UpdateEmailRequestDTO requestDTO) {
        BaseResponse<Void> response = accountService.updateEmail(requestDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        BaseResponse<Void> response = accountService.resetPassword(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
