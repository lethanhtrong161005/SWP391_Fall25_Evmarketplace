package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.config.ConfigResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Config {
    @Id
    @Column(name="cfg_key", length=100)
    private String cfgKey;

    @Column(name="cfg_value", nullable=false, length=255)
    private String value;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    public ConfigResponseDto toDto(Config cfg){
        ConfigResponseDto configResponseDto = new ConfigResponseDto();
        configResponseDto.setCfgKey(cfg.getCfgKey());
        configResponseDto.setCfgKey(cfg.getCfgKey());
        configResponseDto.setCfgValue(cfg.getValue());
        return configResponseDto;
    }

}
