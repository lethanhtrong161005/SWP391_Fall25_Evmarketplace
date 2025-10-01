package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.brand;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.brand.CreateBrandRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.brand.BrandResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Brand;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.BrandStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public BaseResponse<?> getAllBrand() {
        List<BrandResponseDTO> result = brandRepository.findAll().stream().map(item -> item.toDTO(item)).collect(Collectors.toList());
        if (result.isEmpty()) {
            throw new CustomBusinessException("Brand not found");
        }
        BaseResponse<List<BrandResponseDTO>> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("Get brands");
        response.setData(result);
        return response;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public BaseResponse<?> addBrand(CreateBrandRequest createBrandRequest) {
        try{
            Brand brand = new Brand();
            brand.setName(createBrandRequest.getName());
            brand.setStatus(BrandStatus.ACTIVE);
            brandRepository.save(brand);
            BaseResponse<Void> response = new BaseResponse<>();
            response.setSuccess(true);
            response.setStatus(200);
            response.setMessage("Add brand");
            return response;
        }catch (Exception e){
            throw new CustomBusinessException("Failed to create brand");
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public BaseResponse<?> deleteBrand(Long id) {
        try{
            Brand brand = brandRepository.findById(String.valueOf(id)).orElseThrow(() -> new CustomBusinessException("Brand not found"));
            brand.setStatus(BrandStatus.HIDDEN);
            brandRepository.save(brand);
            BaseResponse<Void> response = new BaseResponse<>();
            response.setSuccess(true);
            response.setStatus(200);
            response.setMessage("Delete brand");
            return response;
        }catch (Exception e){
            throw new CustomBusinessException("Failed to delete brand");
        }
    }

}
