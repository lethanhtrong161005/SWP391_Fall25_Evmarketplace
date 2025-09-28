package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoredFile {
    private String originalName;
    private String storedName;
    private String contentType;
    private long size;
}
