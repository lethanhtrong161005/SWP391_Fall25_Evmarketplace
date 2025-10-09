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

    private ConfigResponseDto findByKey(String key) {
        return configRepository.findById(key)
                .map(item -> {
                            return item.toDto(item);
                        }
                ).orElse(null);
    }

}
