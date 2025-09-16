package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.account;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.GoogleUserInfoDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.RegisterAccountRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.LoginResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.OtpResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.PhoneOtp;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Profile;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountRole;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.AccountRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.PhoneOtpRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.JwtUtil;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class AccountServiceImp implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PhoneOtpRepository phoneOtpRepository;
    @Autowired
    private SmsUtil smsUtil;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public BaseResponse<String> sendOtp(String phoneNumber) {
        if (accountRepository.existsByPhoneNumber(phoneNumber)) {
            throw new CustomBusinessException("Phone number already exists");
        }

        String otp = generateOtp();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(1);


        PhoneOtp phoneOtp = phoneOtpRepository.findByPhoneNumber(phoneNumber)
                .orElse(new PhoneOtp());

        phoneOtp.setPhoneNumber(phoneNumber);
        phoneOtp.setOtp(otp);
        phoneOtp.setExpiredAt(expiredAt);
        phoneOtp.setIsUsed(false);
        phoneOtp.setTempToken(null);
        phoneOtp.setTokenExpiredAt(null);

        phoneOtpRepository.save(phoneOtp);

        boolean isSendOtp = smsUtil.sendOtpSms(phoneNumber, otp);

        BaseResponse<String> baseResponse = new BaseResponse<>();
        if (isSendOtp) {
            baseResponse.setSuccess(true);
            baseResponse.setMessage("Send OTP successfully");
            baseResponse.setStatus(200);
        } else {
            baseResponse.setSuccess(false);
            baseResponse.setMessage("Send OTP failed");
            baseResponse.setStatus(400);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<OtpResponse> verifyOtp(String phoneNumber, String otp) {
        PhoneOtp phoneOtp = phoneOtpRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CustomBusinessException("OTP not found for this phone number"));

        LocalDateTime now = LocalDateTime.now();

        if (phoneOtp.getOtp() == null || !phoneOtp.getOtp().equals(otp)) {
            throw new CustomBusinessException("Invalid OTP");
        }

        if (phoneOtp.getExpiredAt().isBefore(now)) {
            throw new CustomBusinessException("OTP has expired");
        }

        if (phoneOtp.getIsUsed()) {
            throw new CustomBusinessException("OTP is already used");
        }

        String tempToken = UUID.randomUUID().toString();
        phoneOtp.setTempToken(tempToken);
        phoneOtp.setTokenExpiredAt(now.plusMinutes(10));
        phoneOtp.setIsUsed(true);
        phoneOtp.setUsedAt(now);

        phoneOtp.setOtp(null);

        phoneOtpRepository.save(phoneOtp);

        OtpResponse otpResponse = new OtpResponse();
        otpResponse.setTempToken(tempToken);

        BaseResponse<OtpResponse> response = new BaseResponse<>();
        response.setData(otpResponse);
        response.setSuccess(true);
        response.setMessage("Verify OTP successfully");
        response.setStatus(200);

        return response;
    }

    @Transactional
    @Override
    public BaseResponse<LoginResponse> registerAccount(RegisterAccountRequest request) {
        PhoneOtp phoneOtp = phoneOtpRepository.findByTempToken(request.getTempToken());
        if (phoneOtp == null) {
            throw new CustomBusinessException("Token invalid");
        }

        if (phoneOtp.getTokenExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CustomBusinessException("Token has expired");
        }

        String phoneNumber = phoneOtp.getPhoneNumber();
        if (accountRepository.existsByPhoneNumber(phoneNumber)) {
            throw new CustomBusinessException("Phone number already exists");
        }

        Account account = new Account();
        account.setPhoneNumber(phoneNumber);
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setPhoneVerified(true);
        account.setRole(AccountRole.MEMBER);
        account.setStatus(AccountStatus.ACTIVE);

        Profile profile = new Profile();
        profile.setFullName(request.getFullName());
        account.setProfile(profile);
        profile.setAccount(account);


        phoneOtp.setTempToken(null);
        phoneOtpRepository.save(phoneOtp);

        Account savedAccount = accountRepository.save(account);

        String accessToken = jwtUtil.generateToken(savedAccount, savedAccount.getProfile());
        String refreshToken = jwtUtil.generateRefreshToken(savedAccount);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(accessToken);
        loginResponse.setRefreshToken(refreshToken);

        BaseResponse<LoginResponse> response = new BaseResponse<>();
        response.setData(loginResponse);
        response.setSuccess(true);
        response.setMessage("Register account successfully");
        response.setStatus(200);
        return response;
    }

    @Transactional
    @Override
    public Account upsertUser(GoogleUserInfoDTO userInfo) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(userInfo.getEmail());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();

            // update account
            account.setGoogleId(userInfo.getId());
            account.setEmailVerified(Boolean.TRUE.equals(userInfo.getVerified_email()));
            account.setStatus(AccountStatus.ACTIVE);

            // update profile
            Profile profile = account.getProfile();
            if (profile == null) {
                profile = new Profile();
                profile.setAccount(account);
            }
            profile.setFullName(userInfo.getName());
            profile.setAvatarUrl(userInfo.getPicture());
            account.setProfile(profile);

            return accountRepository.save(account);
        }else{
            // create new
            Account newAccount = new Account();
            newAccount.setEmail(userInfo.getEmail());
            newAccount.setGoogleId(userInfo.getId());
            newAccount.setRole(AccountRole.MEMBER);
            newAccount.setStatus(AccountStatus.ACTIVE);
            newAccount.setEmailVerified(Boolean.TRUE.equals(userInfo.getVerified_email()));
            newAccount.setPhoneVerified(false);

            Profile profile = new Profile();
            profile.setAccount(newAccount);
            profile.setFullName(userInfo.getName());
            profile.setAvatarUrl(userInfo.getPicture());

            newAccount.setProfile(profile);

            return accountRepository.save(newAccount);
        }
    }

    private String generateOtp() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

}
