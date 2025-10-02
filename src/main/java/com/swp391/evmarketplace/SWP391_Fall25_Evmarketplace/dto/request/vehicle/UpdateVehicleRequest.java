package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.*;
import jakarta.validation.Valid;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // chỉ gửi field cần cập nhật
public class UpdateVehicleRequest {

    // KHÔNG cho đổi category/brand/model trong update MVP
    // Nếu cần, thêm các field này và xử lý service cẩn thận.

    private String name;
    private String description;
    private Integer releaseYear;

    // Spec chung (must-have) – tất cả OPTIONAL để partial update
    private BigDecimal batteryCapacityKwh;
    private Integer     rangeKm;
    private BigDecimal  motorPowerKw;
    private BigDecimal  acChargingKw;
    private BigDecimal  dcChargingKw;  // null nghĩa là set null (nếu gửi vào)

    private AcConnectorType acConnector;
    private DcConnectorType dcConnector;

    // Detail theo loại – nếu gửi, chỉ các field ≠ null mới cập nhật
    @Valid private CarDetailUpdate  carDetail;
    @Valid private BikeDetailUpdate bikeDetail;
    @Valid private EbikeDetailUpdate ebikeDetail;

    // ===== Ô tô (EV_CAR) =====
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CarDetailUpdate {
        private Integer    seatingCapacity;
        private BodyType   bodyType;
        private Drivetrain drivetrain;
        private Integer    trunkRearL;
    }

    // ===== Xe máy (E_MOTORBIKE) =====
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BikeDetailUpdate {
        private MotorLocation motorLocation;
        private String        wheelSize;
        private BrakeType     brakeType;
        private BigDecimal    weightKg;
    }

    // ===== E-bike (E_BIKE) =====
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EbikeDetailUpdate {
        private String      frameSize;
        private String      wheelSize;
        private BigDecimal  weightKg;
        private Integer     maxLoad;
        private Integer     gears;             // sẽ map sang Short
        private Boolean     removableBattery;
        private Boolean     throttle;
    }

}
