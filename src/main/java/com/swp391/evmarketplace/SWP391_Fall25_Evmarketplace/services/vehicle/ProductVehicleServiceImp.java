package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.vehicle;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.vehicle.CreateVehicleRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.vehicle.UpdateVehicleRequest;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.custom.BaseResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.vehicle.BikeDetailResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.vehicle.CarDetailResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.vehicle.EbikeDetailResponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.vehicle.VehicleListReponse;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.*;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.BrandRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.CategoryRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ModelRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ProductVehicleRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductVehicleServiceImp implements ProductVehicleService {
    @Autowired
    private ProductVehicleRepository productVehicleRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<?> getAllAdaptive(Integer page, Integer size, String sortBy, String dir) {
        Sort sort = buildSafeSort(sortBy, dir);

        // 2) Nếu có page/size -> paginate; ngược lại lấy tối đa 1000
        final int SAFE_LIMIT = 1000;
        boolean paged = (page != null && size != null);
        Pageable pageable = paged
                ? PageRequest.of(Math.max(0, page), Math.max(1, size), sort)
                : PageRequest.of(0, SAFE_LIMIT, sort);

        Page<ProductVehicle> pg = productVehicleRepository.findAllWithGraph(pageable);
        List<ProductVehicle> rows = pg.getContent();

        List<VehicleListReponse> items = rows.stream()
                .map(this::toVehicleListResponse)
                .toList();

        BaseResponse<Object> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage(items.isEmpty() ? "No vehicles" : "Vehicles");

        if (paged) {
            var payload = new java.util.HashMap<String, Object>();
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

    private Sort buildSafeSort(String sortBy, String dir) {
        Set<String> allowed = Set.of(
                "id", "name", "releaseYear", "createdAt", "updatedAt",
                "batteryCapacityKwh", "rangeKm", "motorPowerKw"
        );
        String prop = (sortBy != null && allowed.contains(sortBy)) ? sortBy : "createdAt";
        boolean asc = "asc".equalsIgnoreCase(dir);
        return asc ? Sort.by(prop).ascending() : Sort.by(prop).descending();
    }

    private VehicleListReponse toVehicleListResponse(ProductVehicle pv) {
        VehicleListReponse.VehicleListReponseBuilder b = VehicleListReponse.builder()
                .id(pv.getId())
                .category(pv.getCategory() != null ? pv.getCategory().getName() : null)
                .brand(pv.getBrand() != null ? pv.getBrand().getName() : null)
                .model(pv.getModel() != null ? pv.getModel().getName() : null)
                .name(pv.getName())
                .releaseYear(pv.getReleaseYear())
                .batteryCapacityKwh(pv.getBatteryCapacityKwh())
                .rangeKm(pv.getRangeKm())
                .motorPowerKw(pv.getMotorPowerKw())
                .acChargingKw(pv.getAcChargingKw())
                .dcChargingKw(pv.getDcChargingKw())
                .acConnector(pv.getAcConnector())
                .dcConnector(pv.getDcConnector())
                .status(pv.getStatus() != null ? pv.getStatus().toString() : null);

        if (pv.getCarDetail() != null) {
            var d = pv.getCarDetail();
            b.car(CarDetailResponse.builder()
                    .seatingCapacity(d.getSeatingCapacity())
                    .bodyType(d.getBodyType())
                    .drivetrain(d.getDrivetrain())
                    .trunkRearL(d.getTrunkRearL())
                    .build());
        }
        if (pv.getBikeDetail() != null) {
            var d = pv.getBikeDetail();
            b.bike(BikeDetailResponse.builder()
                    .motorLocation(d.getMotorLocation())
                    .wheelSize(d.getWheelSize())
                    .brakeType(d.getBrakeType())
                    .weightKg(d.getWeightKg())
                    .build());
        }
        if (pv.getEbikeDetail() != null) {
            var d = pv.getEbikeDetail();
            b.ebike(EbikeDetailResponse.builder()
                    .frameSize(d.getFrameSize())
                    .wheelSize(d.getWheelSize())
                    .weightKg(d.getWeightKg())
                    .maxLoad(d.getMaxLoad())
                    .gears(d.getGears())
                    .removableBattery(d.getRemovableBattery())
                    .throttle(d.getThrottle())
                    .build());
        }
        return b.build();
    }

    @Override
    @Transactional
    public BaseResponse<?> addVehicle(CreateVehicleRequest request) {
        try{
            String name = request.getName().trim();

            Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new CustomBusinessException("Category not found"));
            Brand brand = brandRepository.findById(request.getBrandId()).orElseThrow(() -> new CustomBusinessException("Brand not found"));
            Model model = modelRepository.findById(request.getModelId()).orElseThrow(() -> new CustomBusinessException("Model not found"));

            if (model.getBrand() == null || !model.getBrand().getId().equals(brand.getId())) {
                throw new CustomBusinessException("Model does not belong to brand");
            }
            if (model.getCategory() == null || !model.getCategory().getId().equals(category.getId())) {
                throw new CustomBusinessException("Model does not belong to category");
            }

            boolean duplicated = productVehicleRepository
                    .existsByBrand_IdAndModel_IdAndNameIgnoreCaseAndReleaseYear(
                            brand.getId(), model.getId(), name, request.getReleaseYear());
            if (duplicated) {
                throw new CustomBusinessException("Vehicle already exists (brand/model/name/year)");
            }

            ProductVehicle pv = new ProductVehicle();
            pv.setCategory(category);
            pv.setBrand(brand);
            pv.setModel(model);
            pv.setName(name);
            pv.setDescription(request.getDescription());
            pv.setReleaseYear(request.getReleaseYear());

            pv.setBatteryCapacityKwh(request.getBatteryCapacityKwh());
            pv.setRangeKm(request.getRangeKm());
            pv.setMotorPowerKw(request.getMotorPowerKw());
            pv.setAcChargingKw(request.getAcChargingKw());
            pv.setDcChargingKw(request.getDcChargingKw());

            pv.setAcConnector(request.getAcConnector());
            pv.setDcConnector(request.getDcConnector());

            String categoryName = category.getName();
            switch (categoryName) {
                case "EV_CAR" -> {
                    var d = Optional.ofNullable(request.getCarDetail())
                            .orElseThrow(() -> new CustomBusinessException("carDetail is required for EV_CAR"));
                    ProductCarDetail car = new ProductCarDetail();
                    car.setSeatingCapacity(d.getSeatingCapacity());
                    car.setBodyType(d.getBodyType());         // enum BodyType
                    car.setDrivetrain(d.getDrivetrain());     // enum Drivetrain  <<<<<< THÊM
                    car.setTrunkRearL(d.getTrunkRearL());
                    pv.setCarDetail(car); // @MapsId sẽ set 2 chiều trong setter
                }
                case "E_MOTORBIKE" -> {
                    var d = Optional.ofNullable(request.getBikeDetail())
                            .orElseThrow(() -> new CustomBusinessException("bikeDetail is required for E_MOTORBIKE"));
                    ProductBikeDetail bike = new ProductBikeDetail();
                    bike.setMotorLocation(d.getMotorLocation()); // enum HUB/MID
                    bike.setWheelSize(d.getWheelSize());
                    bike.setBrakeType(d.getBrakeType());         // enum DISC/DRUM
                    bike.setWeightKg(d.getWeightKg());
                    pv.setBikeDetail(bike);
                }
                case "E_BIKE" -> {
                    var d = Optional.ofNullable(request.getEbikeDetail())
                            .orElseThrow(() -> new CustomBusinessException("ebikeDetail is required for E_BIKE"));
                    ProductEbikeDetail ebike = new ProductEbikeDetail();
                    ebike.setFrameSize(d.getFrameSize());
                    ebike.setWheelSize(d.getWheelSize());
                    ebike.setWeightKg(d.getWeightKg());
                    ebike.setMaxLoad(d.getMaxLoad());
                    ebike.setGears(d.getGears().shortValue());   // TINYINT
                    ebike.setRemovableBattery(d.getRemovableBattery());
                    ebike.setThrottle(d.getThrottle());
                    pv.setEbikeDetail(ebike);
                }
                default -> throw new CustomBusinessException("Unsupported category: " + categoryName);
            }

            productVehicleRepository.save(pv);

            BaseResponse<Void> response = new BaseResponse<>();
            response.setMessage("Successfully added vehicle " + pv.getName());
            response.setSuccess(true);
            response.setStatus(200);
            return response;
        }catch (Exception e){
            throw new CustomBusinessException("Adding vehicle failed");
        }
    }

    @Override
    public BaseResponse<?> updateVehicle(Long id, UpdateVehicleRequest req) {
        // 1) Load vehicle với graph để có sẵn detail
        ProductVehicle pv = productVehicleRepository.findWithGraphById(id);
        if (pv == null) throw new CustomBusinessException("Vehicle not found: " + id);

        // 2) Nếu tên/year thay đổi → check trùng (trên brand/model cố định)
        String newName = req.getName() != null ? req.getName().trim() : null;
        Integer newYear = req.getReleaseYear() != null ? req.getReleaseYear() : pv.getReleaseYear();
        if (newName != null && !newName.equalsIgnoreCase(pv.getName())) {
            boolean dup = productVehicleRepository
                    .existsByBrand_IdAndModel_IdAndNameIgnoreCaseAndReleaseYearAndIdNot(
                            pv.getBrand().getId(), pv.getModel().getId(), newName, newYear, pv.getId());
            if (dup) throw new CustomBusinessException("Vehicle already exists (brand/model/name/year)");
        } else if (req.getReleaseYear() != null) {
            // name giữ nguyên nhưng year đổi → vẫn cần check trùng
            boolean dup = productVehicleRepository
                    .existsByBrand_IdAndModel_IdAndNameIgnoreCaseAndReleaseYearAndIdNot(
                            pv.getBrand().getId(), pv.getModel().getId(), pv.getName(), req.getReleaseYear(), pv.getId());
            if (dup) throw new CustomBusinessException("Vehicle already exists (brand/model/name/year)");
        }

        // 3) Apply partial fields (CHUNG)
        if (newName != null) pv.setName(newName);
        if (req.getDescription() != null) pv.setDescription(req.getDescription());
        if (req.getReleaseYear() != null) pv.setReleaseYear(req.getReleaseYear());

        if (req.getBatteryCapacityKwh() != null) pv.setBatteryCapacityKwh(req.getBatteryCapacityKwh());
        if (req.getRangeKm() != null)             pv.setRangeKm(req.getRangeKm());
        if (req.getMotorPowerKw() != null)        pv.setMotorPowerKw(req.getMotorPowerKw());
        if (req.getAcChargingKw() != null)        pv.setAcChargingKw(req.getAcChargingKw());
        if (req.getDcChargingKw() != null || req.getDcChargingKw() == null) {
            // cho phép set null dcChargingKw: nếu client gửi trường này trong request
            if (req.getDcChargingKw() != null || hasField(req, "dcChargingKw")) {
                pv.setDcChargingKw(req.getDcChargingKw());
            }
        }
        if (req.getAcConnector() != null) pv.setAcConnector(req.getAcConnector());
        if (req.getDcConnector() != null) pv.setDcConnector(req.getDcConnector());
        if (req.getStatus() != null) pv.setStatus(req.getStatus());
        // 4) Apply detail theo category (không cho đổi category trong update)
        String code = pv.getCategory().getName(); // hoặc getCode()
        switch (code) {
            case "EV_CAR" -> {
                if (req.getCarDetail() != null) {
                    var d = req.getCarDetail();
                    ProductCarDetail car = pv.getCarDetail();
                    if (car == null) { car = new ProductCarDetail(); pv.setCarDetail(car); }
                    if (d.getSeatingCapacity() != null) car.setSeatingCapacity(d.getSeatingCapacity());
                    if (d.getBodyType() != null)        car.setBodyType(d.getBodyType());
                    if (d.getDrivetrain() != null)      car.setDrivetrain(d.getDrivetrain());
                    if (d.getTrunkRearL() != null)      car.setTrunkRearL(d.getTrunkRearL());
                }
            }
            case "E_MOTORBIKE" -> {
                if (req.getBikeDetail() != null) {
                    var d = req.getBikeDetail();
                    ProductBikeDetail bike = pv.getBikeDetail();
                    if (bike == null) { bike = new ProductBikeDetail(); pv.setBikeDetail(bike); }
                    if (d.getMotorLocation() != null) bike.setMotorLocation(d.getMotorLocation());
                    if (d.getWheelSize() != null)     bike.setWheelSize(d.getWheelSize());
                    if (d.getBrakeType() != null)     bike.setBrakeType(d.getBrakeType());
                    if (d.getWeightKg() != null)      bike.setWeightKg(d.getWeightKg());
                }
            }
            case "E_BIKE" -> {
                if (req.getEbikeDetail() != null) {
                    var d = req.getEbikeDetail();
                    ProductEbikeDetail ebike = pv.getEbikeDetail();
                    if (ebike == null) { ebike = new ProductEbikeDetail(); pv.setEbikeDetail(ebike); }
                    if (d.getFrameSize() != null)          ebike.setFrameSize(d.getFrameSize());
                    if (d.getWheelSize() != null)          ebike.setWheelSize(d.getWheelSize());
                    if (d.getWeightKg() != null)           ebike.setWeightKg(d.getWeightKg());
                    if (d.getMaxLoad() != null)            ebike.setMaxLoad(d.getMaxLoad());
                    if (d.getGears() != null)              ebike.setGears(d.getGears().shortValue());
                    if (d.getRemovableBattery() != null)   ebike.setRemovableBattery(d.getRemovableBattery());
                    if (d.getThrottle() != null)           ebike.setThrottle(d.getThrottle());
                }
            }
            default -> throw new CustomBusinessException("Unsupported category: " + code);
        }

        // 5) Save
        productVehicleRepository.save(pv);

        BaseResponse<Void> res = new BaseResponse<>();
        res.setSuccess(true);
        res.setStatus(200);
        res.setMessage("Vehicle updated: " + pv.getName());
        return res;
    }

    private boolean hasField(UpdateVehicleRequest req, String fieldName) {
        return true;
    }


}

