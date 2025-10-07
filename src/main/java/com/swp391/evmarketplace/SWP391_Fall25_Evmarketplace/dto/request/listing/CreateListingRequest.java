package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.listing;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Status;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Visibility;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateListingRequest {

    @NotNull
    private Long categoryId;

    @NotBlank
    private String categoryCode;   // EV_CAR / E_MOTORBIKE / E_BIKE / BATTERY

    @NotNull
    private ItemType itemType;     // VEHICLE | BATTERY

    @NotBlank
    private String brand;
    private Long brandId;

    @NotBlank
    private String model;
    private Long modelId;

    @NotBlank
    private String title;

    @Positive
    private Integer year;

    private String color;

    @DecimalMin("0.0") @Digits(integer = 10, fraction = 2)
    private BigDecimal batteryCapacityKwh;

    @DecimalMin("0.0") @DecimalMax("100.0") @Digits(integer = 3, fraction = 2)
    private BigDecimal sohPercent;

    @PositiveOrZero
    private Integer mileageKm;

    @NotNull @DecimalMin("0.0") @Digits(integer = 10, fraction = 2)
    private BigDecimal price;

    @Size(max = 3000)
    private String description;

    @NotBlank private String province;
    @NotBlank private String district;
    @NotBlank private String ward;
    @NotBlank private String address;


    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotBlank
    private String postType;              // FREE | PAID

    @NotNull
    private Visibility visibility;        // NORMAL | BOOSTED


}
