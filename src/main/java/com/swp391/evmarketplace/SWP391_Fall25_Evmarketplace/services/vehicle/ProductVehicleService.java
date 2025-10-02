package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.vehicle;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.vehicle.CreateVehicleRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.vehicle.UpdateVehicleRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;

public interface ProductVehicleService {
    BaseResponse<?> getAllAdaptive(Integer page, Integer size, String sortBy, String dir);

    BaseResponse<?> addVehicle(CreateVehicleRequest request);

    BaseResponse<?> updateVehicle(Long id, UpdateVehicleRequest req);
}
