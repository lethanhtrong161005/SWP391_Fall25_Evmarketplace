package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoredContractResult {
    private String fileName;
    private String sha256;
    private Long sizeBytes;
}
