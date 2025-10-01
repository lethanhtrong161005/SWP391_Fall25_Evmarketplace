package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.profile;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.profile.UpdateProfileRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.StoredFile;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.profile.ProfileResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Profile;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ErrorCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.AccountRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.PhoneOtpRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ProfileRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class ProfileServiceImp implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PhoneOtpRepository phoneOtpRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private FileService fileService;

    @Value("${server.url}")
    private String serverUrl;

    @Override
    public BaseResponse<ProfileResponseDTO> updateProfile(UpdateProfileRequestDTO dto) {
        Account account = authUtil.getCurrentAccount();


        Optional<Profile> optProfile = profileRepository.findById(account.getProfile().getId());

        if (optProfile.isEmpty()) throw new CustomBusinessException("ACCOUNT_NOT_FOUND");

        Profile profile = optProfile.get();

        if (profile.getAccount().isPhoneVerified()) {

            if (dto.getFullName() != null && !dto.getFullName().trim().isEmpty()) {
                profile.setFullName(dto.getFullName());
            }

            if (dto.getProvince() != null && !dto.getProvince().isEmpty()) {
                profile.setProvince(dto.getProvince());
            }
            if (dto.getAddressLine() != null && !dto.getAddressLine().isEmpty()) {
                profile.setAddressLine(dto.getAddressLine());
            }
            profileRepository.save(profile);
        } else {
            throw new CustomBusinessException("ACCOUNT_PHONE_NOT_VERIFIED");
        }
        ProfileResponseDTO profileResponseDTO = new ProfileResponseDTO();
        profileResponseDTO.setFullName(profile.getFullName());
        profileResponseDTO.setProvince(profile.getProvince());
        profileResponseDTO.setAddressLine(profile.getAddressLine());
        profileResponseDTO.setCreateAt(profile.getCreatedAt());
        profileResponseDTO.setUpdateAt(profile.getUpdatedAt());

        BaseResponse<ProfileResponseDTO> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("Updated");
        response.setData(profileResponseDTO);

        return response;
    }

    @Transactional
    @Override
    public BaseResponse<String> updateAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomBusinessException(ErrorCode.AVATAR_FILE_REQUIRED.toString());
        }

        Account account = authUtil.getCurrentAccount();

        Optional<Profile> optProfile = profileRepository.findById(account.getProfile().getId());
        if (optProfile.isEmpty()) throw new CustomBusinessException("ACCOUNT_NOT_FOUND");

        Profile profile = optProfile.get();

        if (!profile.getAccount().isPhoneVerified()) {
            throw new CustomBusinessException("ACCOUNT_PHONE_NOT_VERIFIED");
        }

       StoredFile saved = new StoredFile();
       try{
           fileService.deleteImage(profile.getAvatarUrl());
           saved = fileService.storeImage(file);
       }catch (Exception e){
           throw new CustomBusinessException("STORE_IMAGE_FAILED");
       }

        profile.setAvatarUrl(saved.getStoredName());
        profileRepository.save(profile);

        BaseResponse<String> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("AVATAR_UPDATED");
        response.setData(saved.getStoredName());
        return response;
    }



}
