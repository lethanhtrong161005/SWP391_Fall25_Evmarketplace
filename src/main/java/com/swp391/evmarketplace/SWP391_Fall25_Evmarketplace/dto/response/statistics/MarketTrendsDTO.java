package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketTrendsDTO {

    private String from;
    private String to;
    private String currency;

    // 1) Phân loại loại bài: free, booster
    private Map<String, Long> postType;

    // 2) Phân loại theo loại sản phẩm (cate)
    private Map<String, Long> categoryCount;

    // 3) Phân tích theo thương hiệu & model (top)
    private List<NameCount> topBrands;
    private List<NameCount> topModels;

    // 4) Giá trung bình theo loại sản phẩm
    private Map<String, BigDecimal> avgPriceByCategory;
}
