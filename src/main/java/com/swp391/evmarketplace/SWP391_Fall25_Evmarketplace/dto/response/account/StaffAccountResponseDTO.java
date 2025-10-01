package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StaffAccountResponseDTO {
    private String phoneNumber;
    private String password;
    private String fullName;
}
