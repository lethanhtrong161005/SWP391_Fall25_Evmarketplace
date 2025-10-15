package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftAvailabilityDayDTO {
    private LocalDate date;               // Ngày
    private List<ShiftAvailabilityDTO> shifts; // Danh sách ca và trạng thái đã đặt hay chưa
}
