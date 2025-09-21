package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.profile;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.profile.UpdateProfileRequestDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.profile.ProfileResponseDTO;

public interface ProfileService {
    BaseResponse<ProfileResponseDTO> updateProfile(UpdateProfileRequestDTO updateProfileRequestDTO);
}
