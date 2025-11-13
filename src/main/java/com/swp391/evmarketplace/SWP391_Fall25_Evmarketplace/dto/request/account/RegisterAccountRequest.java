package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterAccountRequest {
    @NotBlank(message = "Token is required")
    private String tempToken;


    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,32}$",
            message = "Password must be 8-32 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character (!@#$%^&*)"
    )
    private String password;

    @NotBlank(message = "Full name is required")
    @Pattern(
            regexp = "^[\\p{L}][\\p{L}\\s\\-']{1,49}$",
            message = "Full name must be 2-50 characters and contain only letters, spaces, hyphens or apostrophes"
    )
    private String fullName;
}
