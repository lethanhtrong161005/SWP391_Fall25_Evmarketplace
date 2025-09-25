package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.profile;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.profile.UpdateProfileRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.profile.ProfileResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Profile;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ErrorCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.AccountRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.PhoneOtpRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ProfileRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.file.FileService;
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
    ProfileRepository profileRepository;

    @Autowired
    PhoneOtpRepository phoneOtpRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    FileService fileService;

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


        String fileName = fileService.saveOrReplaceAvatar(account.getId(), file);
        String v;
        try {
            v = md5Hex(file.getBytes());
        } catch (IOException e) {
            v = String.valueOf(System.currentTimeMillis());
        }

        profile.setAvatarUrl(fileName);
        profileRepository.save(profile);

        String avatarUrl = serverUrl + "/api/accounts/" + fileName + "/avatar?v=" + v;

        BaseResponse<String> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("AVATAR_UPDATED");
        response.setData(avatarUrl);
        return response;
    }


    private static String md5Hex(byte[] bytes) {
        try {
            var md = java.security.MessageDigest.getInstance("MD5");
            var dig = md.digest(bytes);
            var sb = new StringBuilder();
            for (byte b : dig) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return Long.toHexString(System.currentTimeMillis());
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Resource> viewAvatar(String fileName) {
        // Tìm profile có avatarUrl khớp với fileName
        Optional<Profile> optProfile = profileRepository.findByAvatarUrl(fileName);
        if(optProfile == null || optProfile.isEmpty()) throw new CustomBusinessException(ErrorCode.AVATAR_NOT_FOUND.toString());

        Profile profile = optProfile.get();

        Resource res = fileService.loadAvatar(profile.getAccount().getId());
        if (res == null || !res.exists() || !res.isReadable()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Đoán content-type
        String contentType;
        try {
            contentType = Files.probeContentType(res.getFile().toPath());
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + res.getFilename() + "\"")
                .cacheControl(CacheControl.noCache().cachePublic()) // đảm bảo FE thấy ảnh mới nếu v đổi
                .body(res);
    }


}
