package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.validation.ValidPhone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyOtpDTO {
    @NotBlank(message = "Phone number is required")
    @ValidPhone
    private String phoneNumber;

    @NotBlank
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be exactly 6 digits")
    private String otp;
}
