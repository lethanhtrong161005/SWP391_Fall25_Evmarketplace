package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateStaffAccountRequestDTO {
    @NotBlank(message = "PHONE_NUMBER_REQUIRED")
    private String phoneNumber;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,32}$",
            message = "PASSWORD_INVALID"
    )
    private String password;

    @NotBlank(message = "FULL_NAME_REQUIRED")
    @Pattern(
            regexp = "^[\\p{L}][\\p{L}\\s\\-']{1,49}$",
            message = "FULL_NAME_INVALID"
    )
    private String fullName;
}
