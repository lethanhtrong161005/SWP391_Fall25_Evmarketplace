package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UpdateProfileRequestDTO {
    @Size(max = 255, message = "PROFILE.FULL_NAME_TOO_LONG")
    private String fullName;

    @Size(max = 500, message = "PROFILE.AVATAR_URL_TOO_LONG")
    private String avatarUrl;

    @Size(max = 100, message = "PROFILE.PROVINCE_TOO_LONG")
    private String province;

    @Size(max = 255, message = "PROFILE.ADDRESS_LINE_TOO_LONG")
    private String addressLine;
}
