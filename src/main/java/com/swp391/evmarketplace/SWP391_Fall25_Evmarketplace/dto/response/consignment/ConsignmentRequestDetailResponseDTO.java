package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.consignment;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Branch;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Category;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.CategoryCode;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ConsignmentRequestStatus;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.ItemType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ConsignmentRequestDetailResponseDTO {
    private Account owner;
    private ItemType itemType;
    private Category category;
    private String brand;
    private String model;
    private Integer year;
    private BigDecimal batteryCapacityKwh;
    private BigDecimal sohPercent;
    private Integer mileageKm;
    private Branch preferredBranch;
    private BigDecimal ownerExpectedPrice;
    private String note;
    private ConsignmentRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> mediaUrl;
}


