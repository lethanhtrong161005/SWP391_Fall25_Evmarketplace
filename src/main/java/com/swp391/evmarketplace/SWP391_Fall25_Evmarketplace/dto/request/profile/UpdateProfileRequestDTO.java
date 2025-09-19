package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.profile;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Gender;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.validation.ValidPhone;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.validation.birthday.ValidBirthday;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UpdateProfileRequestDTO {
    @Size(max = 255, message = "full name no more than 255 characters")
    private String fullName;

    @Size(max = 50, message = "nick name no more than 50 characters")
    private String nickName;

    private Gender gender;

    @ValidBirthday(
            pattern = "yyyy-MM-dd",
            past = true,
            minAge = 15,
            maxAge = 150,
            message = "Birthday must be valid day in the past. You must larger 15"
    )
    private String birthDate;

    @Size(max = 300, message = "Bio no more than 300 characters ")
    private String bio;

    @Size(max = 255, message = "avatar no more than 255 characters")
    private String avatarUrl;
}
