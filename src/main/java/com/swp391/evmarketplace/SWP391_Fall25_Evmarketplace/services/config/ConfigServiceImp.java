package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.config;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.config.ConfigResponseDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ConfigServiceImp implements ConfigService {
    @Autowired
    private ConfigRepository configRepository;

    @Override
    public BaseResponse<Map<String, ConfigResponseDto>> getFeeListingConfig() {
        ConfigResponseDto activeDayBoosted = findByKey("listing_active_days_boosted");
        ConfigResponseDto fee = findByKey("promoted_fee_vnd");
        ConfigResponseDto activeDayNormal = findByKey("listing_active_days_normal");
        Map<String, ConfigResponseDto> result = new HashMap<>();
        result.put("activeDayBoosted", activeDayBoosted);
        result.put("fee", fee);
        result.put("activeDayNormal", activeDayNormal);
        BaseResponse<Map<String, ConfigResponseDto>> configResponseDtoBaseResponse = new BaseResponse<>();
        configResponseDtoBaseResponse.setData(result);
        configResponseDtoBaseResponse.setSuccess(true);
        configResponseDtoBaseResponse.setMessage("Get Fee Boosted Successfully !");
        configResponseDtoBaseResponse.setStatus(200);
        return configResponseDtoBaseResponse;
    }

    public ConfigResponseDto findByKey(String key) {
        return configRepository.findById(key)
                .map(item -> {
                            return item.toDto(item);
                        }
                ).orElseThrow(() -> new CustomBusinessException("Config not found!"));
    }


    public int getModerationLockTtlSecs() {
        int def = 600; // fallback
        int min = 60, max = 3600; // 1–60 phút

        var cfg = findByKey("moderation_lock_ttl_secs");
        int v = def;
        if (cfg != null && cfg.getCfgValue() != null) {
            try { v = Integer.parseInt(cfg.getCfgValue().trim()); } catch (Exception ignored) {}
        }
        return Math.max(min, Math.min(max, v));
    }

}
