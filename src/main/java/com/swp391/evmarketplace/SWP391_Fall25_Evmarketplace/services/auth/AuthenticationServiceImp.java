package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.auth;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.auth.LoginRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.auth.GoogleUserInfoDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.GoogleCallbackRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.auth.LoginResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.AccountRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.account.AccountService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;



@Service
public class AuthenticationServiceImp implements AuthenticationService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AccountService accountService;

    private RestTemplate restTemplate = new RestTemplate();

//    Google
    @Value("${google.authUri}")
    private String googleAuthUri;
    @Value("${google.clientId}")
    private String googleClientId;
    @Value("${google.clientSecret}")
    private String googleClientSecret;
    @Value("${google.redirectUri}")
    private String googleRedirectUri;
    @Value("${google.tokenUri}")
    private String googleTokenUri;
    @Value("${google.userInfoUri}")
    private String googleUserInfoUri;


    @Override
    public BaseResponse<LoginResponse> loginWithPhoneNumber(LoginRequest request) {

        Optional<Account> optionalAccount = accountRepository.findByPhoneNumber(request.getPhoneNumber());
        if (optionalAccount.isEmpty()) {
            throw new CustomBusinessException("Phone number not found");
        }

        if (!passwordEncoder.matches(request.getPassword(), optionalAccount.get().getPassword())) {
            throw new CustomBusinessException("Wrong password");
        }

        String accessToken = jwtUtil.generateToken(optionalAccount.get(), optionalAccount.get().getProfile());
        String refreshToken = jwtUtil.generateRefreshToken(optionalAccount.get());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(accessToken);
        loginResponse.setRefreshToken(refreshToken);
        BaseResponse<LoginResponse> baseResponse = new BaseResponse<>();
        baseResponse.setData(loginResponse);
        baseResponse.setMessage("Login successful");
        baseResponse.setSuccess(true);
        baseResponse.setStatus(200);
        return baseResponse;
    }

    @Override
    public BaseResponse<String> generateGoogleAuthUrl() {
        BaseResponse<String> baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);
        baseResponse.setStatus(200);
        baseResponse.setMessage("Google Auth Url generated");
        String url = UriComponentsBuilder.fromHttpUrl(googleAuthUri) // v2: https://accounts.google.com/o/oauth2/v2/auth
                .queryParam("client_id", googleClientId)
                .queryParam("redirect_uri", googleRedirectUri) // sẽ được encode đúng
                .queryParam("response_type", "code")
                .queryParam("scope", "openid email profile")    // để nguyên khoảng trắng ở đây
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build()                                       // ❗ không dùng build(true)
                .encode(StandardCharsets.UTF_8)                // ❗ bắt buộc encode
                .toUriString();
        baseResponse.setData(url);
        return baseResponse;
    }

    @Override
    public BaseResponse<LoginResponse> googleLogin(GoogleCallbackRequest request) {
        BaseResponse<LoginResponse> baseResponse = new BaseResponse();
        try{
            String rawCode = URLDecoder.decode(request.getCode(), StandardCharsets.UTF_8);
            Map<String,Object> tokenResponse = exchangeCodeForToken(rawCode);

            String accessToken = (String) tokenResponse.get("access_token");

            GoogleUserInfoDTO accountInfo = getAccountInfo(accessToken);

            Account account = accountService.upsertUser(accountInfo);

            LoginResponse loginResponse = new LoginResponse();
            String jwtAccessToken = jwtUtil.generateToken(account, account.getProfile());
            String refreshToken = jwtUtil.generateRefreshToken(account);
            loginResponse.setAccessToken(jwtAccessToken);
            loginResponse.setRefreshToken(refreshToken);
            baseResponse.setData(loginResponse);
            baseResponse.setMessage("Login successful");
            baseResponse.setSuccess(true);
            baseResponse.setStatus(200);

        }catch (Exception e){
            throw new CustomBusinessException(e.getMessage());
        }
        return baseResponse;
    }



    // change code -> access_token
    private Map<String, Object> exchangeCodeForToken(String code){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("client_id", googleClientId);
        formData.add("client_secret", googleClientSecret);
        formData.add("redirect_uri", googleRedirectUri);
        formData.add("grant_type", "authorization_code");

        return restTemplate.postForObject(googleTokenUri, new HttpEntity<>(formData, headers), Map.class);
    }

    //get info from accessToken of google
    private GoogleUserInfoDTO getAccountInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        return restTemplate.exchange(
                googleUserInfoUri,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                GoogleUserInfoDTO.class
        ).getBody();
    }

}
