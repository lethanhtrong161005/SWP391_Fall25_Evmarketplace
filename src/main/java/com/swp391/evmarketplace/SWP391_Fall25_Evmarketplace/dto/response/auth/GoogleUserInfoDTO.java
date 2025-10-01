package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.auth;

import lombok.Data;

@Data
public class GoogleUserInfoDTO {
    private String id;
    private String email;
    private String name;
    private String picture;
    private Boolean verified_email;
}
