package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NameCount {
    private String name;
    private long count;
}
