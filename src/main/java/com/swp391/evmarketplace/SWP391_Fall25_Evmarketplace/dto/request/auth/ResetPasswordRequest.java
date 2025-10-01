package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank(message = "New password is required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,32}$",
            message = "Password must be 8-32 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character (!@#$%^&*)"
    )
    private String newPassword;
}
