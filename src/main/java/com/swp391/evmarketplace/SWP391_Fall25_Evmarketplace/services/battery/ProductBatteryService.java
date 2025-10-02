package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.battery;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.battery.CreateBatteryRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.battery.UpdateBatteryRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;

public interface ProductBatteryService {
    BaseResponse<?> addBattery(CreateBatteryRequest req);

    BaseResponse<?> getAllAdaptive(Integer page, Integer size, String sortBy, String dir);

    BaseResponse<?> updateBattery(Long id, UpdateBatteryRequest req);

}
