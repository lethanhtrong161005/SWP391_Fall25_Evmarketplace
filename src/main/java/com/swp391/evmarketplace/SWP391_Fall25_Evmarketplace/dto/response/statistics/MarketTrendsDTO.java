package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class MarketTrendsDTO {

    private String from;                 // yyyy-MM-dd hoặc null nếu lấy tất cả
    private String to;                   // yyyy-MM-dd hoặc null
    private String currency;             // "VND"

    // 1) Phân loại loại bài: free, booster
    private Map<String, Long> postTypeBreakdown;          // {"FREE": 123, "BOOSTER": 45}

    // 2) Phân loại theo loại sản phẩm
    private Map<String, Long> categoryBreakdown;          // {"BATTERY": 30, "CAR": 80, ...}

    // 3) Phân tích theo thương hiệu & model (top N)
    private List<NameCount> topBrands;                    // [{"name":"VinFast","count":40}, ...]
    private List<NameCount> topModels;                    // [{"name":"VF e34","count":18}, ...]

    // 4) Giá trung bình theo loại sản phẩm
    private Map<String, BigDecimal> avgListingPriceByCategory; // {"BATTERY":"12,500,000.00", ...}

}
