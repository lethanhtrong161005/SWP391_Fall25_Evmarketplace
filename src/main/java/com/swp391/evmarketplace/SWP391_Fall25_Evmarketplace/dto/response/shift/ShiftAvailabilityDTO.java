package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.shift;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftAvailabilityDTO {
    private Long shiftId;
    private String code;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean booked; // true if already booked at given date
}
