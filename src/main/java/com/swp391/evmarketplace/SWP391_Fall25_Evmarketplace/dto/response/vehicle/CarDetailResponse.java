package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.vehicle;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.BodyType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.Drivetrain;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarDetailResponse {
    private Integer seatingCapacity;
    private BodyType bodyType;
    private Drivetrain drivetrain;
    private Integer trunkRearL;
}
