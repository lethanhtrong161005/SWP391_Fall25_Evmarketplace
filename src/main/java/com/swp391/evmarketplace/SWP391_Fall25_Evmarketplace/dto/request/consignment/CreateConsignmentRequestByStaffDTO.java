package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.request.consignment;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.validation.ValidPhone;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateConsignmentRequestByStaffDTO extends CreateConsignmentRequestDTO {
    @ValidPhone
    private String phone;
}
