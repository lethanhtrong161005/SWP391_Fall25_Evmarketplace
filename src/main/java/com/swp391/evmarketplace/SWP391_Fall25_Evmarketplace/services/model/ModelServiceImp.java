package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.model;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.model.CreateModelRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.model.UpdateModelRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.model.ModelDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Brand;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Category;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Model;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ModelStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.BrandRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.CategoryRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ModelServiceImp implements ModelService {

    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;

    @Override
    public BaseResponse<?> getAllModels() {
        List<ModelDTO> result = modelRepository.findAll().stream().map(item -> {
            return item.toDTO(item);
        }).toList();
        if (result.isEmpty()) {
            throw new CustomBusinessException("There are no models");
        }
        BaseResponse<List<ModelDTO>> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("Models");
        response.setData(result);
        return response;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public BaseResponse<?> addModel(CreateModelRequest request) {

        String name = request.getName().trim();

        if (modelRepository.existsByNameIgnoreCaseAndBrandIdAndCategoryIdAndYear(
                name, request.getBrandId(), request.getCategoryId(), request.getYear())) {
            throw new CustomBusinessException("Model already exists for this brand/category/year");
        }

        Brand brand = brandRepository.findById(String.valueOf(request.getBrandId()))
                .orElseThrow(() -> new CustomBusinessException("Brand not found: " + request.getBrandId()));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomBusinessException("Category not found: " + request.getCategoryId()));

        Model model = new Model();
        model.setName(name);
        model.setYear(request.getYear());
        model.setBrand(brand);
        model.setCategory(category);
        model.setStatus(ModelStatus.ACTIVE);

        modelRepository.save(model);

        BaseResponse<Void> response = new BaseResponse<>();
        response.setSuccess(true);
        response.setStatus(200);
        response.setMessage("Model added");
        return response;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public BaseResponse<?> updateModel(UpdateModelRequest request, Long id) {
        try{
            Model model = modelRepository.findById(id).orElseThrow(() -> new CustomBusinessException("Model not found"));
            if(request.getName() != null && !request.getName().trim().isEmpty()){
                if(!modelRepository.existsByNameIgnoreCase(request.getName().trim())){
                    model.setName(request.getName().trim());
                }
            }
            if(request.getYear() != null){
                model.setYear(request.getYear());
            }

            if(request.getBrandId() != null){
                Brand b = brandRepository.findById(String.valueOf(request.getBrandId())).orElseThrow(() -> new CustomBusinessException("Brand not found"));
                model.setBrand(b);
            }
            if(request.getCategoryId() != null){
                Category c =  categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new CustomBusinessException("Category not found"));
                model.setCategory(c);
            }
           if(request.getStatus() != null){
               model.setStatus(request.getStatus());
           }
            modelRepository.save(model);
            BaseResponse<Void> response = new BaseResponse<>();
            response.setSuccess(true);
            response.setStatus(200);
            response.setMessage("Model updated");
            return response;
        }catch (Exception e) {
            throw new CustomBusinessException("Failed to update model");
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public BaseResponse<?> deleteModel(Long id) {
        try{
            Model model = modelRepository.findById(id).orElseThrow(() -> new CustomBusinessException("Model not found"));
            model.setStatus(ModelStatus.HIDDEN);
            modelRepository.save(model);
            BaseResponse<Void> response = new BaseResponse<>();
            response.setSuccess(true);
            response.setStatus(200);
            response.setMessage("Model deleted");
            return response;
        }catch (Exception e) {
            throw new CustomBusinessException("Failed to delete model");
        }
    }
}
