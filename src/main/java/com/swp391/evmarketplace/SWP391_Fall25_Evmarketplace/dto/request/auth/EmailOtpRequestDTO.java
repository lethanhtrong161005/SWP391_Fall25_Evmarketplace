package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailOtpRequestDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String Email;
}
