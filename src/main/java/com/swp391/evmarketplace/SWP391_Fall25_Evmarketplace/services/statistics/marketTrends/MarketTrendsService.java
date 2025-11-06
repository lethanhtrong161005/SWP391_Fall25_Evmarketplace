package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.marketTrends;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.MarketTrendsDTO;

import java.time.LocalDate;

public interface MarketTrendsService {
    MarketTrendsDTO getMarketTrends(LocalDate from, LocalDate to, Integer topLimit);
}
