package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.vehicle;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.BodyType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Drivetrain;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarDetail {
    @NotNull
    @Min(1)
    private Integer seatingCapacity;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BodyType bodyType;       // SEDAN|SUV|HATCHBACK|MPV|PICKUP|OTHER

    @NotNull
    @Enumerated(EnumType.STRING)
    private Drivetrain drivetrain;   // FWD|RWD|AWD

    @NotNull @Min(0)
    private Integer trunkRearL;      // L
}
