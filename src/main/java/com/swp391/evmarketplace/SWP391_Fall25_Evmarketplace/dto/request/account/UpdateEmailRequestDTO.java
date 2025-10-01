package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateEmailRequestDTO {
    @Email(message = "INVALID_EMAIL")
    @NotBlank(message = "EMAIL_NOT_BE_BLANK")
    private String newEmail;
}
