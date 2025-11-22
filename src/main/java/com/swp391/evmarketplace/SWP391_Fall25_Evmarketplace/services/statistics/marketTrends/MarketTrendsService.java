package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.marketTrends;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.MarketTrendsDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.NameCount;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MarketTrendsService {
    MarketTrendsDTO getMarketTrends(LocalDate from, LocalDate to, Integer topLimit);

    Map<String, Long> countPostType(LocalDate from, LocalDate to);
    Map<String, Long> countByCategory(LocalDate from, LocalDate to);
    List<NameCount> topBrands(LocalDate from, LocalDate to, int limit);
    List<NameCount> topModels(LocalDate from, LocalDate to, int limit);
    Map<String, BigDecimal> avgPriceByCategory(LocalDate from, LocalDate to);

}
