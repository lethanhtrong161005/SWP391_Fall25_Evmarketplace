package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment.agreement;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ConsignmentAgreementDTO {
    private Long id;
    private Long requestId;
    private String ownerName;
    private String phone;
    private String staffName;
    private String branchName;
    private BigDecimal commissionPercent;
    private BigDecimal acceptablePrice;
    private String status;
    private String duration;
    private String medialUrl;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expireAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
