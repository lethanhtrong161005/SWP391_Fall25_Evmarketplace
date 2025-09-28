package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.profile;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.profile.UpdateProfileRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.profile.ProfileResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    BaseResponse<ProfileResponseDTO> updateProfile(UpdateProfileRequestDTO updateProfileRequestDTO);
    BaseResponse<String> updateAvatar(MultipartFile file);
}
