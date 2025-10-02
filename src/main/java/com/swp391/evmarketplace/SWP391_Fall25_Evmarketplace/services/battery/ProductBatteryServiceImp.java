package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.battery;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.battery.CreateBatteryRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.battery.UpdateBatteryRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.battery.BatteryListResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Brand;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Category;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Model;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.ProductBattery;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ProductStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.BrandRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.CategoryRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ModelRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ProductBatteryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ProductBatteryServiceImp implements ProductBatteryService {

    @Autowired
    private ProductBatteryRepository productBatteryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional
    public BaseResponse<?> addBattery(CreateBatteryRequest req) {
        try{
            Model model = modelRepository.findById(req.getModelId())
                    .orElseThrow(() -> new CustomBusinessException("Model not found"));
            Brand brand = model.getBrand();
            Category category = model.getCategory();
            if (brand == null || category == null) {
                throw new CustomBusinessException("Model is missing brand/category");
            }
            if (!"BATTERY".equalsIgnoreCase(category.getName())) {
                throw new CustomBusinessException("Model belongs to " + category.getName() + ", expected BATTERY");
            }

            BigDecimal cap  = scale2(req.getCapacityKwh());
            BigDecimal volt = scale2(req.getVoltage());
            BigDecimal weight = scale2(req.getWeightKg());

            if (productBatteryRepository.existsByBrand_IdAndModel_IdAndCapacityKwhAndVoltage(
                    brand.getId(), model.getId(), cap, volt)) {
                throw new CustomBusinessException("Battery already exists (brand/model/capacity/voltage)");
            }

            ProductBattery pb = ProductBattery.builder()
                    .brand(brand)
                    .model(model)
                    .category(category)
                    .chemistry(safeTrim(req.getChemistry()))
                    .capacityKwh(cap)
                    .voltage(volt)
                    .weightKg(weight)
                    .dimension(safeTrim(req.getDimension()))
                    .status(ProductStatus.ACTIVE) // enum phải là ACTIVE/HIDDEN
                    .build();

            productBatteryRepository.saveAndFlush(pb);

            BaseResponse<Void> res = new BaseResponse<>();
            res.setSuccess(true);
            res.setStatus(200);
            res.setMessage("Battery added (id=" + pb.getId() + ")");
            return res;
        } catch (Exception e) {
            throw new CustomBusinessException(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<?> getAllAdaptive(Integer page, Integer size, String sortBy, String dir) {
        Sort sort = buildSafeSort(sortBy, dir);

        final int SAFE_LIMIT = 1000;
        boolean paged = (page != null && size != null);
        Pageable pageable = paged
                ? PageRequest.of(Math.max(0, page), Math.max(1, size), sort)
                : PageRequest.of(0, SAFE_LIMIT, sort);

        Page<ProductBattery> pg = productBatteryRepository.findAllWithGraph(pageable);

        List<BatteryListResponse> items = pg.getContent().stream()
                .map(this::toBatteryListResponse)
                .toList();

        BaseResponse<Object> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage(items.isEmpty() ? "No batteries" : "Batteries");

        if (paged) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("items", items);
            payload.put("page", pg.getNumber());
            payload.put("size", pg.getSize());
            payload.put("totalElements", pg.getTotalElements());
            payload.put("totalPages", pg.getTotalPages());
            res.setData(payload);
        } else {
            res.setData(items);
        }
        return res;
    }

    @Override
    @Transactional
    public BaseResponse<?> updateBattery(Long id, UpdateBatteryRequest req) {

        ProductBattery pb = productBatteryRepository.findById(id)
                .orElseThrow(() -> new CustomBusinessException("Battery not found: " + id));

        Model targetModel = pb.getModel();
        if (req.getModelId() != null && !req.getModelId().equals(pb.getModel().getId())) {
            targetModel = modelRepository.findById(req.getModelId())
                    .orElseThrow(() -> new CustomBusinessException("Model not found: " + req.getModelId()));
        }

        Brand targetBrand = targetModel.getBrand();
        Category targetCategory = targetModel.getCategory();
        if (targetBrand == null || targetCategory == null) {
            throw new CustomBusinessException("Target model is missing brand/category");
        }
        if (!"BATTERY".equalsIgnoreCase(targetCategory.getName())) {
            throw new CustomBusinessException("Model belongs to " + targetCategory.getName() + ", expected BATTERY");
        }

        // 3) Duplicate-check với giá trị "sắp set"
        //    (nếu field không truyền → dùng giá trị hiện tại)
        var newCapacity = (req.getCapacityKwh() != null) ? scale2(req.getCapacityKwh()) : pb.getCapacityKwh();
        var newVoltage  = (req.getVoltage()     != null) ? scale2(req.getVoltage())     : pb.getVoltage();
        Long newBrandId = targetBrand.getId();
        Long newModelId = targetModel.getId();

        boolean dup = productBatteryRepository
                .existsByBrand_IdAndModel_IdAndCapacityKwhAndVoltageAndIdNot(
                        newBrandId, newModelId, newCapacity, newVoltage, pb.getId());
        if (dup) {
            throw new CustomBusinessException("Battery already exists (brand/model/capacity/voltage)");
        }

        // 4) Áp thay đổi (partial update)
        //    - Nếu đổi modelId → brand/category tự đồng bộ theo model đích
        if (!targetModel.getId().equals(pb.getModel().getId())) {
            pb.setModel(targetModel);
            pb.setBrand(targetBrand);
            pb.setCategory(targetCategory); // luôn là BATTERY
        }

        if (hasText(req.getChemistry()))  pb.setChemistry(req.getChemistry().trim());
        if (req.getCapacityKwh() != null) pb.setCapacityKwh(newCapacity);
        if (req.getVoltage()     != null) pb.setVoltage(newVoltage);
        if (req.getWeightKg()    != null) pb.setWeightKg(scale2(req.getWeightKg()));
        if (hasText(req.getDimension()))  pb.setDimension(req.getDimension().trim());

        if (hasText(req.getStatus())) {
            try {
                pb.setStatus(ProductStatus.valueOf(req.getStatus().trim().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new CustomBusinessException("Invalid status: " + req.getStatus());
            }
        }

        productBatteryRepository.save(pb);

        BaseResponse<Void> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("Battery updated (id=" + pb.getId() + ")");
        return res;
    }

    private Sort buildSafeSort(String sortBy, String dir) {
        Set<String> allowed = Set.of(
                "id", "createdAt", "capacityKwh", "voltage", "weightKg"
                // có thể thêm "chemistry" nếu muốn sort theo text
        );
        String prop = (sortBy != null && allowed.contains(sortBy)) ? sortBy : "createdAt";
        boolean asc = "asc".equalsIgnoreCase(dir);
        return asc ? Sort.by(prop).ascending() : Sort.by(prop).descending();
    }

    private BatteryListResponse toBatteryListResponse(ProductBattery pb) {
        return BatteryListResponse.builder()
                .id(pb.getId())
                .brand(pb.getBrand() != null ? pb.getBrand().getName() : null)
                .model(pb.getModel() != null ? pb.getModel().getName() : null)
                .category(pb.getCategory() != null ? pb.getCategory().getName() : null)
                .chemistry(pb.getChemistry())
                .capacityKwh(pb.getCapacityKwh())
                .voltage(pb.getVoltage())
                .weightKg(pb.getWeightKg())
                .dimension(pb.getDimension())
                .status(pb.getStatus() != null ? pb.getStatus().name() : null)
                .createdAt(pb.getCreatedAt())
                .build();
    }

    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private  String safeTrim(String s) {
        return (s == null) ? null : s.trim();
    }

    private BigDecimal scale2(BigDecimal v) {
        return (v == null) ? null : v.setScale(2, RoundingMode.HALF_UP);
    }

}
