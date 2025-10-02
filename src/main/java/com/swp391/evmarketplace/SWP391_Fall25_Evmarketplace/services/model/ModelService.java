package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.model;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.model.CreateModelRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.model.UpdateModelRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;

public interface ModelService {
    BaseResponse<?> getAllModels();

    BaseResponse<?> addModel(CreateModelRequest request);
    BaseResponse<?> updateModel(UpdateModelRequest request, Long id);
    BaseResponse<?> deleteModel(Long id);
}
