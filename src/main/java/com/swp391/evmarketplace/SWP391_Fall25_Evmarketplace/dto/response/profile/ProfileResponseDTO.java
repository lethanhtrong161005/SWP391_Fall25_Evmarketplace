package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.profile;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDTO {
    private String fullName;
    private String avatarUrl;
    private String province;
    private String addressLine;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public ProfileResponseDTO fromEntity(Profile profile) {
        return new ProfileResponseDTO(
                profile.getFullName(),
                profile.getAvatarUrl(),
                profile.getProvince(),
                profile.getAddressLine(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
