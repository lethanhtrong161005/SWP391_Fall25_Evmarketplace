package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.brand;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.brand.CreateBrandRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.brand.UpdateBrandRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.brand.BrandResponseDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Brand;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Category;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.CategoryBrand;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.BrandStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.BrandRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.CategoryBrandRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandServiceImp implements BrandService {

    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryBrandRepository categoryBrandRepository;

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<?> getAllBrand() {
        var brands = brandRepository.findAll();
        var data = brands.stream().map(this::toDTO).toList();

        BaseResponse<Object> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage(data.isEmpty() ? "No brands" : "Brands");
        res.setData(data);
        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<?> getAllBrandByCategoryId(Long categoryId) {
        // validate category tồn tại
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomBusinessException("Category not found: " + categoryId));

        var brands = brandRepository.findByCategoryId(categoryId);
        var data = brands.stream().map(this::toDTO).toList();

        BaseResponse<Object> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage(data.isEmpty() ? "No brands in category" : "Brands by category");
        res.setData(data);
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<?> addBrand(CreateBrandRequest req) {
        String name = req.getName().trim();
        if (brandRepository.existsByNameIgnoreCase(name)) {
            throw new CustomBusinessException("Brand name already exists");
        }

        Brand brand = new Brand();
        brand.setName(name);
        brand.setStatus(req.getStatus() != null ? req.getStatus() : BrandStatus.ACTIVE);
        brandRepository.save(brand);

        upsertBrandCategories(brand, req.getCategoryIds());

        BaseResponse<Void> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("Add brand successfully");
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<?> updateBrand(Long id, UpdateBrandRequest req) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new CustomBusinessException("Brand not found"));

        if (req.getName() != null && !req.getName().trim().isEmpty()) {
            String newName = req.getName().trim();
            if (brandRepository.existsByNameIgnoreCaseAndIdNot(newName, id)) {
                throw new CustomBusinessException("Brand name already exists");
            }
            brand.setName(newName);
        }
        if (req.getStatus() != null) {
            brand.setStatus(req.getStatus());
        }
        brandRepository.save(brand);

        // Nếu client gửi categoryIds → REPLACE mapping
        if (req.getCategoryIds() != null) {
            upsertBrandCategories(brand, req.getCategoryIds());
        }

        BaseResponse<Void> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("Update brand successfully");
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<?> deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new CustomBusinessException("Brand not found"));
        brand.setStatus(BrandStatus.HIDDEN);
        brandRepository.save(brand);

        BaseResponse<Void> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("Delete brand (soft)");
        return res;
    }

    private void upsertBrandCategories(Brand brand, List<Long> categoryIds) {
        if (categoryIds == null) return; // không đụng tới mapping nếu client không gửi

        if (categoryIds.isEmpty()) {
            // Xoá hết mapping nếu truyền []
            categoryBrandRepository.deleteByBrand_Id(brand.getId());
            return;
        }

        // Validate và chuẩn bị list hợp lệ
        List<Long> keep = new ArrayList<>();
        for (Long cid : categoryIds) {
            Category c = categoryRepository.findById(cid)
                    .orElseThrow(() -> new CustomBusinessException("Category not found: " + cid));
            keep.add(c.getId());
        }

        // Xoá các mapping thừa
        categoryBrandRepository.deleteByBrand_IdAndCategory_IdNotIn(brand.getId(), keep);

        // Thêm các mapping thiếu
        for (Long cid : keep) {
            if (!categoryBrandRepository.existsByCategory_IdAndBrand_Id(cid, brand.getId())) {
                CategoryBrand cb = CategoryBrand.builder()
                        .brand(brand)
                        .category(categoryRepository.getReferenceById(cid))
                        .build();
                categoryBrandRepository.save(cb);
            }
        }
    }

    private BrandResponseDTO toDTO(Brand b) {
        var links = categoryBrandRepository.findByBrand_Id(b.getId());
        var catIds = links.stream().map(l -> l.getCategory().getId()).toList();

        return BrandResponseDTO.builder()
                .id(b.getId())
                .name(b.getName())
                .status(b.getStatus().name())
                .categoryIds(catIds)
                .build();
    }

}
