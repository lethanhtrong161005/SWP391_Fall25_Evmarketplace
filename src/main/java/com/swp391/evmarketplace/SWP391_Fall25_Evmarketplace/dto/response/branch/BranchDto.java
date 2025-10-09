package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.branch;

import lombok.Data;

@Data
public class BranchDto {
    private Long id;
    private String name;
    private String province;
    private String address;
    private String phone;
    private String status;
}
