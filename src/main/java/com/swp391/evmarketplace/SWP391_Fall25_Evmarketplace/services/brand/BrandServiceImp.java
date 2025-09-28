package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.brand;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.brand.BrandResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImp implements BrandService {
    @Autowired
    private BrandRepository brandRepository;

    @Override
    public BaseResponse<List<BrandResponseDTO>> getAllBrandByCategoryId(Long id) {
        List<BrandResponseDTO> result = brandRepository.findByCategoryId(id)
                .stream()
                .map(item -> item.toDTO(item))
                .toList();
        if (result.isEmpty()) {
            throw new CustomBusinessException("Brand not found with category id " + id);
        }
        BaseResponse<List<BrandResponseDTO>> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("Get brands with category id " + id);
        response.setData(result);
        return response;
    }


}
