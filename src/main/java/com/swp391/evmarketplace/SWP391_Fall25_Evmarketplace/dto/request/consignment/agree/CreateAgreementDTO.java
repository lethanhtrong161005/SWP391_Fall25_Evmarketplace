package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment.agree;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.AgreementDuration;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAgreementDTO {
    @NotNull
    Long requestId;

    @NotNull
    @Positive
    @DecimalMin("0.0")
    BigDecimal commissionPercent;

    @NotNull
    @Positive
    @DecimalMin("0.0")
    @Digits(integer = 10, fraction = 2)
    BigDecimal acceptablePrice;
    @NotNull
    LocalDateTime startAt;

    @NotNull
    AgreementDuration duration;
}
