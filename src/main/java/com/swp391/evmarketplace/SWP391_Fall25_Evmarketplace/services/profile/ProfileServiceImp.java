package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.profile;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.profile.UpdateProfileRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.profile.ProfileResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.PhoneOtp;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Profile;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.AccountRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.PhoneOtpRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ProfileRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProfileServiceImp implements ProfileService {

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    PhoneOtpRepository phoneOtpRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AuthUtil authUtil;

    @Override
    public BaseResponse<ProfileResponseDTO> updateProfile(UpdateProfileRequestDTO dto) {
        Account account = authUtil.getCurrentAccount();


        Optional<Profile> optProfile = profileRepository.findById(account.getProfile().getId());

        if (optProfile.isEmpty()) throw new CustomBusinessException("Update request not found !!");

        Profile profile = optProfile.get();

        if (profile.getAccount().isPhoneVerified()) {
            if (dto.getFullName() != null && !dto.getFullName().trim().isEmpty()) {
                profile.setFullName(dto.getFullName());
            }

            if (dto.getNickName() != null && !dto.getNickName().trim().isEmpty()) {
                profile.setNickname(dto.getNickName());
            }

            if (dto.getGender() != null) {
                profile.setGender(dto.getGender());
            }

            if (dto.getBirthDate() != null) {
                profile.setBirthDate(LocalDate.parse(dto.getBirthDate()));
            }

            if (dto.getBio() != null && !dto.getBio().isEmpty()) {
                profile.setBio(dto.getBio());
            }

            if (dto.getAvatarUrl() != null && !dto.getAvatarUrl().isEmpty()) {
                profile.setAvatarUrl(dto.getAvatarUrl());
            }

            profileRepository.save(profile);
        } else {
            throw new CustomBusinessException("Add your phone number !!");
        }


        ProfileResponseDTO profileResponseDTO = new ProfileResponseDTO();
        profileResponseDTO.setPhoneNumber(profile.getAccount().getPhoneNumber());
        profileResponseDTO.setFullName(profile.getFullName());
        profileResponseDTO.setNickName(profile.getNickname());
        profileResponseDTO.setGender(profile.getGender());
        profileResponseDTO.setBirthDate(profile.getBirthDate());
        profileResponseDTO.setBio(profile.getBio());
        profileResponseDTO.setAvaterUrl(profile.getAvatarUrl());
        profileResponseDTO.setCreateAt(profile.getCreatedAt());
        profileResponseDTO.setUpdateAt(profile.getUpdatedAt());

        BaseResponse<ProfileResponseDTO> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("Updated");
        response.setData(profileResponseDTO);

        return response;
    }
}
