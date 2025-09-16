package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.validation.ValidPhone;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Phone number is required")
    @ValidPhone
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    private String password;

}
