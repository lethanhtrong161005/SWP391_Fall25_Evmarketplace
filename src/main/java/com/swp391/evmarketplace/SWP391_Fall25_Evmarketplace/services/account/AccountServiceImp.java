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
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Branch;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Otp;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Profile;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountRole;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ErrorCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.mapper.AccountMapper;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.AccountRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.BranchRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.OtpRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ProfileRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.JwtUtil;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.SpeedSMSAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AccountServiceImp implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private SpeedSMSAPI speedSMSAPI;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private JavaMailSender mailSender;
    @Value("${server.url}")
    private String serverUrl;
    @Autowired
    private BranchRepository branchRepository;

    @Override
    public BaseResponse<String> sendOtpRegister(String phoneNumber) {
        if (accountRepository.existsByPhoneNumber(phoneNumber)) {
            throw new CustomBusinessException("Phone number already exists");
        }
        return doSendOtp(phoneNumber);
    }

    @Override
    public BaseResponse<?> getAccountCurrent() {
        Account ac = authUtil.getCurrentAccount();
        AccountReponseDTO dto = new AccountReponseDTO();
        BaseResponse<AccountReponseDTO> response = new BaseResponse<>();
        if (ac != null) {
            dto = ac.toDto(ac, serverUrl);
            response.setData(dto);
            response.setMessage("Get Account Success");
            response.setSuccess(true);
            response.setStatus(200);
        } else {
            response.setMessage("Get Account Failed");
            response.setSuccess(false);
            response.setStatus(400);
        }
        return response;
    }

    @Override
    public BaseResponse<String> sendOtpReset(String phoneNumber) {
        if (!accountRepository.existsByPhoneNumber(phoneNumber)) {
            throw new CustomBusinessException("Phone number not exists");
        }
        return doSendOtp(phoneNumber);
    }


    @Override
    public BaseResponse<OtpResponse> verifyOtp(String phoneNumber, String otp) {

        Otp phoneOtp = otpRepository.findByPhoneNumber(phoneNumber)

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

        otpRepository.save(phoneOtp);

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

        Otp otp = otpRepository.findByTempToken(request.getTempToken());

        if (otp == null) {
            throw new CustomBusinessException("Token invalid");
        }

        if (otp.getTokenExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CustomBusinessException("Token has expired");
        }

        String phoneNumber = otp.getPhoneNumber();
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


        otpRepository.save(otp);

        Account savedAccount = accountRepository.save(account);

        otpRepository.delete(otp);


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
        } else {
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

    @Override
    public BaseResponse<Void> changePassword(ChangePasswordRequest request) {
        Account currentAccount = authUtil.getCurrentAccount();

        if (currentAccount == null) {
            throw new CustomBusinessException("Current account is null");
        }

        if (!currentAccount.isPhoneVerified()) {
            throw new CustomBusinessException("Phone number not verified");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), currentAccount.getPassword())) {
            throw new CustomBusinessException("Old password doesn't match");
        }

        if (passwordEncoder.matches(request.getNewPassword(), currentAccount.getPassword())) {
            throw new CustomBusinessException("New password can't be the same old password");
        }

        currentAccount.setPassword(passwordEncoder.encode(request.getNewPassword()));

        accountRepository.save(currentAccount);

        BaseResponse<Void> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setMessage("Change Password successfully");
        response.setStatus(200);
        return response;

    }


    @Override
    @Transactional
    public BaseResponse<Void> resetPassword(ResetPasswordRequest request) {

        Otp otp = otpRepository.findByTempToken(request.getToken());

        if (otp == null) {
            throw new CustomBusinessException("Token invalid");
        }
        if (otp.getTokenExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CustomBusinessException("Token has expired");
        }

        Account account = accountRepository.findByPhoneNumber(otp.getPhoneNumber())
                .orElseThrow(() -> new CustomBusinessException("Token invalid"));

        if (passwordEncoder.matches(request.getNewPassword(), account.getPassword())) {
            throw new CustomBusinessException("Password has been used recently");
        }

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);

        otpRepository.delete(otp);


        BaseResponse<Void> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setMessage("Reset password successfully");
        response.setStatus(200);
        return response;
    }

    private String generateOtp() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    private BaseResponse<String> doSendOtp(String phoneNumber) {
        String otp = generateOtp();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(1);


        Otp phoneOtp = otpRepository.findByPhoneNumber(phoneNumber)

                .orElse(new Otp());

        phoneOtp.setPhoneNumber(phoneNumber);
        phoneOtp.setOtp(otp);
        phoneOtp.setExpiredAt(expiredAt);
        phoneOtp.setIsUsed(false);
        phoneOtp.setTempToken(null);
        phoneOtp.setTokenExpiredAt(null);

        otpRepository.save(phoneOtp);

        String content = "Your OTP is: " + otp;
        boolean isSendOtp = true; //Chỉnh logic chỗ này
        String result = "";
//        try {
//            result = speedSMSAPI.sendSMS(
//                    phoneNumber,
//                    content
//            );
//            isSendOtp = true;
//        } catch (Exception e) {
//            throw new CustomBusinessException("SMS failed");
//        }

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
        baseResponse.setData(result);
        return baseResponse;
    }

    @Override
    public BaseResponse<String> sendOtpEmail(String email) {
        if (accountRepository.existsByEmail(email)) {
            throw new CustomBusinessException(ErrorCode.EMAIL_ALREADY_EXISTS.name());
        }

        String otp = generateOtp();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);

        Otp emailOtp = otpRepository.findByEmail(email).orElse(new Otp());
        emailOtp.setEmail(email);
        emailOtp.setOtp(otp);
        emailOtp.setExpiredAt(expiredAt);
        emailOtp.setIsUsed(false);
        emailOtp.setTempToken(null);
        emailOtp.setTokenExpiredAt(null);

        otpRepository.save(emailOtp);

        boolean isSendOtp = false;
        String result = "";

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("Your OTP Code:");
            msg.setText("Your OTP is: " + otp + ". It will expire in 5 minute");
            mailSender.send(msg);
            isSendOtp = true;
            result = ErrorCode.EMAIL_SENT.name();
        } catch (Exception e) {
            throw new CustomBusinessException(ErrorCode.EMAIL_FAIL.name());
        }

        BaseResponse<String> response = new BaseResponse<>();
        response.setData(result);
        response.setSuccess(isSendOtp);
        response.setStatus(isSendOtp ? 200 : 400);
        response.setMessage(isSendOtp ? ErrorCode.SEND_OTP_SUCCESSFULLY.name() : ErrorCode.SEND_OTP_FAILED.name());

        return response;
    }

    @Override
    public BaseResponse<OtpResponse> verifyEmailOtp(VerifyEmailOtpRequestDTO verifyEmailOtp) {
        Otp emailOtp = otpRepository.findByEmail(verifyEmailOtp.getEmail())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.OTP_NOT_FOUND.name()));

        LocalDateTime now = LocalDateTime.now();

        if (emailOtp.getOtp() == null || !emailOtp.getOtp().equals(verifyEmailOtp.getOtp())) {
            throw new CustomBusinessException(ErrorCode.INVALID_OTP.name());
        }

        if (emailOtp.getExpiredAt().isBefore(now)) {
            throw new CustomBusinessException(ErrorCode.OTP_HAS_EXPIRED.name());
        }

        if (Boolean.TRUE.equals(emailOtp.getIsUsed())) {
            throw new CustomBusinessException(ErrorCode.OTP_ALREADY_USED.name());
        }

        String tempToken = UUID.randomUUID().toString();
        emailOtp.setTempToken(tempToken);
        emailOtp.setTokenExpiredAt(now.plusMinutes(10));
        emailOtp.setIsUsed(true);
        emailOtp.setOtp(null);

        otpRepository.save(emailOtp);

        OtpResponse otpResponse = new OtpResponse();
        otpResponse.setTempToken(tempToken);

        BaseResponse<OtpResponse> response = new BaseResponse<>();
        response.setMessage(ErrorCode.VERIFY_OTP_SUCCESSFULLY.name());
        response.setData(otpResponse);
        response.setSuccess(true);
        response.setStatus(200);
        return response;
    }


    //====================ACCOUNT====================
    @Override
    public BaseResponse<Void> updateEmail(UpdateEmailRequestDTO requestDTO) {
        Otp otp = otpRepository.findByTempToken(requestDTO.getTempToken());

        if (otp == null) {
            throw new CustomBusinessException(ErrorCode.INVALID_TOKEN.name());
        }

        if (otp.getTokenExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CustomBusinessException(ErrorCode.TOKEN_HAS_EXPIRED.name());
        }


        Account account = authUtil.getCurrentAccount();
        if (account == null) {
            throw new CustomBusinessException(ErrorCode.ACCOUNT_NOT_FOUND.name());
        }

        if (!account.isPhoneVerified()) {
            throw new CustomBusinessException(ErrorCode.PHONE_NOT_VERIFIED.name());
        }

        if (accountRepository.existsByEmail(requestDTO.getNewEmail())) {
            throw new CustomBusinessException(ErrorCode.EMAIL_ALREADY_EXISTS.name());
        }

        account.setEmail(requestDTO.getNewEmail());
        accountRepository.save(account);

        BaseResponse<Void> response = new BaseResponse<>();
        response.setMessage(ErrorCode.UPDATED_EMAIL.name());
        response.setStatus(200);
        response.setSuccess(true);

        return response;
    }


    public Pageable buildPageable(int page, int size, String sort, String dir) {
        Sort s = (sort == null || sort.isBlank())
                ? Sort.by(Sort.Direction.DESC, "createdAt") //mặc định show acc mới tạo gần nhất
                : Sort.by("desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC, sort);
        return PageRequest.of(Math.max(page, 0), Math.max(size, 1), s); // số trang 0 âm, ít nhất 1 phần tử trong mỗi trang
    }

    @Override
    //Chỉnh account mappers
    public BaseResponse<Map<String, Object>> getAll(int page, int size, String sort, String dir) {
        Pageable pageable = buildPageable(page, size, sort, dir);
        Page<Account> accounts = accountRepository.findAllAccountBy(pageable);
        if (accounts.isEmpty()) {
            throw new CustomBusinessException(ErrorCode.ACCOUNT_LIST_EMPTY.name());
        }
        List<AccountReponseDTO> items = accounts.getContent()
                .stream().map(item -> {
                    return item.toDto(item, serverUrl);
                })
                .toList();

        Map<String, Object> payload = Map.of(
                "items", items,
                "page", page,
                "size", size,
                "totalPages", accounts.getTotalPages(),
                "totalElements", accounts.getTotalElements(),
                "hasNext", accounts.hasNext(),
                "hasPrevious", accounts.hasPrevious()
        );

        BaseResponse<Map<String, Object>> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setData(payload);
        response.setStatus(200);
        response.setMessage("OK");


        return response;
    }

    @Override
    //Chỉnh account mappers
    public BaseResponse<Map<String, Object>> search(String keyword, int page, int size, String sort, String dir) {
        if (keyword == null || keyword.isBlank()) {
            throw new CustomBusinessException(ErrorCode.KEYWORD_NOT_FOUND.name());
        }
        Pageable pageable = buildPageable(page, size, sort, dir);
        Page<Account> accounts = accountRepository.findByProfileFullNameContainingIgnoreCase(keyword, pageable);
        if (accounts.isEmpty()) {
            throw new CustomBusinessException(ErrorCode.ACCOUNT_NOT_FOUND.name());
        }

        List<AccountReponseDTO> items = accounts.getContent().stream().map(item -> {
            return item.toDto(item, serverUrl);
        }).toList();

        Map<String, Object> payload = Map.of(
                "items", items,
                "page", page,
                "size", size,
                "keyword", keyword,
                "totalPages", accounts.getTotalPages(),
                "totalElements", accounts.getTotalElements(),
                "hasNext", accounts.hasNext(),
                "hasPrevious", accounts.hasPrevious()
        );

        BaseResponse<Map<String, Object>> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setData(payload);
        response.setStatus(200);
        response.setMessage("OK");

        return response;
    }

    @Override
    // Chỉnh account mapper
    public BaseResponse<AccountReponseDTO> getAccountById(Long id) {
        Optional<Account> optAccount = accountRepository.findById(id);
        if (optAccount.isEmpty()) {
            throw new CustomBusinessException(ErrorCode.ACCOUNT_NOT_FOUND.name());
        }
        Account account = optAccount.get();
        AccountReponseDTO accountReponseDTO = account.toDto(account, serverUrl);

        BaseResponse<AccountReponseDTO> response = new BaseResponse<>();
        response.setMessage("OK");
        response.setSuccess(true);
        response.setStatus(200);
        response.setData(accountReponseDTO);

        return response;
    }

    @Override
    public BaseResponse<Void> blockAccount(Long accountId) {
        if (accountId == null) {
            throw new CustomBusinessException("ACCOUNT_ID_NOT_FOUND");
        }
        Optional<Account> OptAcc = accountRepository.findById(accountId);
        if (OptAcc.isEmpty()) throw new CustomBusinessException("ACCOUNT_NOT_FOUND");

        Account account = OptAcc.get();

        if (account.getStatus() == AccountStatus.DELETED) {
            throw new CustomBusinessException("CANNOT_BLOCK_DELETED");
        }

        if (account.getStatus() == AccountStatus.SUSPENDED) {
            BaseResponse<Void> response = new BaseResponse<>();
            response.setMessage("ALREADY_SUSPENDED");
            response.setStatus(200);
            response.setSuccess(true);
            return response;
        }

        account.setStatus(AccountStatus.SUSPENDED);
        accountRepository.save(account);

        BaseResponse<Void> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("ACCOUNT_BLOCKED");
        return response;
    }

    @Override
    public BaseResponse<Void> unblockAccount(Long accountId) {
        if (accountId == null) {
            throw new CustomBusinessException("ACCOUNT_ID_NOT_FOUND");
        }
        Optional<Account> OptAcc = accountRepository.findById(accountId);
        if (OptAcc.isEmpty()) throw new CustomBusinessException("ACCOUNT_NOT_FOUND");

        Account account = OptAcc.get();

        if (account.getStatus() == AccountStatus.DELETED) {
            throw new CustomBusinessException("CANNOT_BLOCK_DELETED");
        }

        if (account.getStatus() == AccountStatus.ACTIVE) {
            BaseResponse<Void> response = new BaseResponse<>();
            response.setMessage("ALREADY_ACTIVE");
            response.setStatus(200);
            response.setSuccess(true);
            return response;
        }

        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);

        BaseResponse<Void> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("ACCOUNT_UNBLOCKED");
        return response;
    }

    @Override
    public BaseResponse<StaffAccountResponseDTO> createStaffAccount(CreateStaffAccountRequestDTO requestDTO) {

        if (accountRepository.existsByPhoneNumber(requestDTO.getPhoneNumber())) {
            throw new CustomBusinessException("PHONE_NUMBER_EXIST");
        }

        Branch branch = branchRepository.findById(requestDTO.getBranchId())
                .orElseThrow(() -> new CustomBusinessException(ErrorCode.BRANCH_NOT_FOUND.name()));

        if (!(requestDTO.getRole().equals(AccountRole.STAFF)) && !(requestDTO.getRole().equals(AccountRole.MODERATOR)))
            throw new CustomBusinessException("No condition to create new account");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Account account = new Account();
        account.setPhoneNumber(requestDTO.getPhoneNumber());
        account.setPassword(encoder.encode(requestDTO.getPassword()));
        account.setBranch(branch);
        account.setRole(requestDTO.getRole());

        Profile profile = new Profile();
        profile.setFullName(requestDTO.getFullName());
        account.setProfile(profile);
        profile.setAccount(account);

        accountRepository.save(account);
        profileRepository.save(profile);

        StaffAccountResponseDTO responseDTO = new StaffAccountResponseDTO();
        responseDTO.setFullName(profile.getFullName());
        responseDTO.setPhoneNumber(account.getPhoneNumber());
        responseDTO.setPassword(account.getPassword());

        BaseResponse<StaffAccountResponseDTO> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setData(responseDTO);

        return response;
    }

    public BaseResponse<List<Account>> getStaffListInBranch() {
        Account account = authUtil.getCurrentAccount();

        List<Account> accounts = accountRepository.findByRoleAndStatusAndBranch_Id(AccountRole.STAFF, AccountStatus.ACTIVE, account.getBranch().getId());
        if (accounts == null || accounts.isEmpty())
            throw new CustomBusinessException(ErrorCode.ACCOUNT_NOT_FOUND.name());

        BaseResponse<List<Account>> response = new BaseResponse<>();
        response.setData(accounts);
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("OK");
        return response;
    }

}
