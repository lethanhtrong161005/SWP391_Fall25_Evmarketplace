package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.profile;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Profile;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDTO {
    private String phoneNumber;
    private String fullName;
    private String nickName;
    private Gender gender;
    private LocalDate birthDate;
    private String bio;
    private String avaterUrl;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public ProfileResponseDTO fromEntity(Profile profile) {
        return new ProfileResponseDTO(
                profile.getAccount().getPhoneNumber(),
                profile.getFullName(),
                profile.getNickname(),
                profile.getGender(),
                profile.getBirthDate(),
                profile.getBio(),
                profile.getAvatarUrl(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
