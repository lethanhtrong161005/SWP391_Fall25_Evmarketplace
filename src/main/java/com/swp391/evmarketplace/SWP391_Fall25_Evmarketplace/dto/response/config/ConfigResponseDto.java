package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfigResponseDto {
    private String cfgKey;
    private String cfgValue;
    private LocalDateTime updatedAt;
}
