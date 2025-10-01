package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.category;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.category.CreateCategoryDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.category.UpdateCategoryRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.brand.BrandResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.brand.BrandWithModelsDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.category.CategoryBrandWithModelsDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.category.CategoryResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.category.CategoryTreeDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.model.ModelDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Category;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.CategoryBrand;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Model;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.CategoryStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.CategoryBrandRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.CategoryRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ModelRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.CategoryBrandFlat;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections.ModelFlat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImp implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryBrandRepository categoryBrandRepository;
    @Autowired
    private ModelRepository modelRepository;

    @Override
    public BaseResponse<List<CategoryResponseDTO>> getAll() {
       List<CategoryResponseDTO> categoryResponseDTOList = categoryRepository.findAll().stream().map(item -> {
           return item.toDto(item);
       }).toList();
       if (categoryResponseDTOList.isEmpty()) {
           throw new CustomBusinessException("Category Not Found");
       }
       BaseResponse<List<CategoryResponseDTO>> response = new BaseResponse<>();
       response.setMessage("Category List");
       response.setSuccess(true);
       response.setStatus(200);
       response.setData(categoryResponseDTOList);
       return response;
    }


    //Tìm kiếm category theo name
    @Override
    public BaseResponse<List<CategoryResponseDTO>> getByNameIn(List<String> names) {
        List<CategoryResponseDTO> result = categoryRepository.findByNameIn(names)
                .stream()
                .map(item -> item.toDto(item))
                .toList();
        if (result.isEmpty()) {
            throw new CustomBusinessException("Filter in Name Not Found");
        }
        BaseResponse<List<CategoryResponseDTO>> response = new BaseResponse<>();
        response.setMessage("Category List In Name");
        response.setSuccess(true);
        response.setStatus(200);
        response.setData(result);
        return response;
    }


    @Override
    public List<CategoryBrand> getAllBrandWithCategoryId(Long id) {
        return categoryBrandRepository.findByCategoryId(id);
    }


    @Override
    public BaseResponse<List<CategoryBrandWithModelsDTO>> getCategoryBrandsWithModels(Long categoryId) {
        List<CategoryBrand> links = categoryBrandRepository.findByCategoryIdFetch(categoryId);
        if (links.isEmpty()) {
            throw new CustomBusinessException("Category Not Found");
        }

        List<Model> allModels = modelRepository.findByCategoryIdOrderByBrandNameAscNameAsc(categoryId);
        Map<Long, List<ModelDTO>> modelsByBrand = allModels.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getBrand().getId(),
                        Collectors.mapping(m -> ModelDTO.builder()
                                        .id(m.getId()).name(m.getName()).year(m.getYear()).build(),
                                Collectors.toList()
                        )
                ));
        List<CategoryBrandWithModelsDTO> data = links.stream()
                .map(cb -> CategoryBrandWithModelsDTO.builder()
                        .id(cb.getId())
                        .category(new CategoryResponseDTO(
                                cb.getCategory().getId(),
                                cb.getCategory().getName(),
                                cb.getCategory().getDescription()
                        ))
                        .brand(new BrandResponseDTO(
                                cb.getBrand().getId(),
                                cb.getBrand().getName()
                        ))
                        .models(modelsByBrand.getOrDefault(cb.getBrand().getId(), List.of()))
                        .build()
                )
                .toList();
        if (data.isEmpty()) {
            throw new CustomBusinessException("Category Not Found");
        }
        BaseResponse<List<CategoryBrandWithModelsDTO>> response = new BaseResponse<>();
        response.setMessage("Category Brand");
        response.setSuccess(true);
        response.setStatus(200);
        response.setData(data);
        return response;
    }


    @Override
    public BaseResponse<List<CategoryTreeDTO>> getCategoryBrandModel() {
        List<CategoryBrandFlat> pairs  = categoryBrandRepository.findAllFlat();
        List<ModelFlat>        models = modelRepository.findAllFlat();

        List<CategoryTreeDTO> data = buildTree(pairs, models);
        BaseResponse<List<CategoryTreeDTO>> response = new BaseResponse<>();
        response.setMessage("Category List");
        response.setSuccess(true);
        response.setStatus(200);
        response.setData(data);
        return response;
    }

    @Override
    public BaseResponse<CategoryTreeDTO> getCategoryBrandModelById(Long categoryId) {
        List<CategoryBrandFlat> pairs = categoryBrandRepository.findFlatByCategoryId(categoryId);
        if (pairs.isEmpty()) throw new CustomBusinessException("Category Not Found");

        List<ModelFlat> models = modelRepository.findAllFlatByCategoryId(categoryId);

        List<CategoryTreeDTO> trees = buildTree(pairs, models);
        if(trees.isEmpty()) throw new CustomBusinessException("Category Not Found");
        BaseResponse<CategoryTreeDTO> res = new BaseResponse<>();
        res.setStatus(200);
        res.setSuccess(true);
        res.setMessage("Category Tree");
        res.setData(trees.get(0));
        return res;
    }

    private List<CategoryTreeDTO> buildTree(
            List<CategoryBrandFlat> pairs,
            List<ModelFlat> models
    ) {
        Map<Long, CategoryTreeDTO> catMap = new LinkedHashMap<>();

        Map<String, BrandWithModelsDTO> brandKeyMap = new HashMap<>();


        for (CategoryBrandFlat p : pairs) {
            CategoryTreeDTO cat = catMap.computeIfAbsent(p.getCategoryId(), id -> {
                CategoryTreeDTO dto = new CategoryTreeDTO();
                dto.setId(p.getCategoryId());
                dto.setName(p.getCategoryName());
                dto.setDescription(p.getCategoryDescription());
                dto.setBrands(new ArrayList<>());
                return dto;
            });

            String key = p.getCategoryId() + "#" + p.getBrandId();
            brandKeyMap.computeIfAbsent(key, k -> {
                BrandWithModelsDTO b = new BrandWithModelsDTO();
                b.setId(p.getBrandId());
                b.setName(p.getBrandName());
                b.setModels(new ArrayList<>());
                cat.getBrands().add(b);
                return b;
            });
        }

        // 2) Gắn Models vào đúng (categoryId, brandId)
        for (ModelFlat m : models) {
            String key = m.getCategoryId() + "#" + m.getBrandId();
            BrandWithModelsDTO b = brandKeyMap.get(key);
            if (b != null) {
                b.getModels().add(
                        ModelDTO.builder()
                                .id(m.getId())
                                .name(m.getName())
                                .year(m.getYear())
                                .build()
                );
            }
        }

        return new ArrayList<>(catMap.values());
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public BaseResponse<Void> addCategory(CreateCategoryDTO request) {
        try{
            BaseResponse<Void> response = new BaseResponse<>();
            Category category = new Category();
            category.setName(request.getName());
            category.setDescription(request.getDescription());
            categoryRepository.save(category);
            response.setMessage("Category Added");
            response.setSuccess(true);
            response.setStatus(200);
            return response;
        }catch (Exception e){
            throw new CustomBusinessException("Failed to add Category");
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public BaseResponse<?> deleteCategory(Long categoryId) {
        try{
            Category category = categoryRepository.findById(categoryId).get();
            category.setStatus(CategoryStatus.HIDDEN);
            categoryRepository.save(category);
            BaseResponse<Void> response = new BaseResponse<>();
            response.setMessage("Category Soft Deleted");
            response.setSuccess(true);
            response.setStatus(200);
            return response;
        }catch (Exception e){
            throw new CustomBusinessException("Failed to delete Category");
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public BaseResponse<?> updateCategory(Long categoryId, UpdateCategoryRequest request) {
        try{
            Category category = categoryRepository.findById(categoryId).get();
            if(request.getName() != null && !request.getName().isEmpty()) category.setName(request.getName());
            if(request.getDescription() != null && !request.getDescription().isEmpty()) category.setDescription(request.getDescription());
            if (request.getStatus() != null) category.setStatus(request.getStatus());
            categoryRepository.save(category);
            BaseResponse<Void> response = new BaseResponse<>();
            response.setMessage("Category Updated");
            response.setSuccess(true);
            response.setStatus(200);
            return response;
        } catch (Exception e) {
            throw new CustomBusinessException("Failed to update Category");
        }
    }

}
