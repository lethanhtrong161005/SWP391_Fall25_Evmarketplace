package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Profile;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountRole;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountReponseDTO {
    private Long id;
    private String phoneNumber;
    private String email;
    private String googleId;
    private AccountRole role;
    private boolean phoneVerified;
    private boolean emailVerified;
    private AccountStatus status;
    private Profile profile;
}
