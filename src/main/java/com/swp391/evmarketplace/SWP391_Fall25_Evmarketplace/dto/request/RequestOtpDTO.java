package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.OtpType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.validation.ValidPhone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestOtpDTO {
    @NotBlank(message = "Phone number is required")
    @ValidPhone
    private String phoneNumber;

    @NotNull(message = "Type is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OtpType type;
}
