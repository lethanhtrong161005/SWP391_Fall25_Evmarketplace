package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.config;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.config.ConfigResponseDto;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;

import java.util.Map;

public interface ConfigService {
    BaseResponse<Map<String, ConfigResponseDto>> getFeeListingConfig();
}
