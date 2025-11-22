package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.marketTrends;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.MarketTrendsDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.NameCount;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ReportMarketPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Service
public class MarketTrendsServiceImp implements MarketTrendsService {

    @Autowired
    private ReportMarketPlaceRepository reportMarketPlaceRepository;

    @Override
    public MarketTrendsDTO getMarketTrends(LocalDate from, LocalDate to, Integer topLimit) {
        boolean noRange = (from == null && to == null);
        if (noRange) {
            from = LocalDate.of(1970, 1, 1);
            to   = LocalDate.of(2100, 1, 1);
        } else if (from == null) {
            from = to.minusDays(30);
        } else if (to == null) {
            to = LocalDate.now();
        }

        MarketTrendsDTO dto = new MarketTrendsDTO();
        dto.setPostType(countPostType(from, to));
        dto.setCategoryCount(countByCategory(from, to));
        dto.setTopBrands(topBrands(from, to, topLimit));
        dto.setTopModels(topModels(from, to, 5));
        dto.setAvgPriceByCategory(avgPriceByCategory(from, to));
        dto.setCurrency("VND");
        dto.setFrom(noRange ? null : from.toString());
        dto.setFrom(noRange ? null : to.toString());
        return dto;
    }

    @Override
    public Map<String, Long> countPostType(LocalDate from, LocalDate to) {
        Timestamp fromTs = start(from);
        Timestamp toPlusTs = end(to);

        List<Object[]> rows = reportMarketPlaceRepository.countPostType(fromTs, toPlusTs);

        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] r : rows) {
            String visibility = r[0] != null ? r[0].toString() : "UNKNOWN";
            Long count = ((Number) r[1]).longValue();
            result.put(visibility, count);
        }
        return result;

    }

    @Override
    public Map<String, Long> countByCategory(LocalDate from, LocalDate to) {
        Timestamp fromTs = start(from);
        Timestamp toPlusTs = end(to);

        List<Object[]> rows = reportMarketPlaceRepository.countByCategory(fromTs, toPlusTs);

        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] r : rows) {
            String categoryName = r[0] != null ? r[0].toString() : "UNKNOWN";
            Long count = ((Number) r[1]).longValue();
            result.put(categoryName, count);
        }
        return result;
    }

    @Override
    public List<NameCount> topBrands(LocalDate from, LocalDate to, int limit) {
        Timestamp fromTs = start(from);
        Timestamp toPlusTs = end(to);

        var brands = reportMarketPlaceRepository.topBrands(fromTs, toPlusTs, limit);
        List<NameCount> topBrands = new ArrayList<NameCount>();
        for (Object[] i : brands) {
            topBrands.add(NameCount.builder()
                    .name(Objects.toString(i[0], "UNKNOW"))
                    .count(((Number) i[1]).longValue())
                    .build());
        }

        return topBrands;
    }

    @Override
    public List<NameCount> topModels(LocalDate from, LocalDate to, int limit) {
        Timestamp fromTs = start(from);
        Timestamp toPlusTs = end(to);

        var brands = reportMarketPlaceRepository.topModels(fromTs, toPlusTs, limit);
        List<NameCount> topModels = new ArrayList<NameCount>();
        for (Object[] i : brands) {
            topModels.add(NameCount.builder()
                    .name(Objects.toString(i[0], "UNKNOW"))
                    .count(((Number) i[1]).longValue())
                    .build());
        }
        return topModels;
    }

    @Override
    public Map<String, BigDecimal> avgPriceByCategory(LocalDate from, LocalDate to) {
        Timestamp fromTs = start(from);
        Timestamp toPlusTs = end(to);

        List<Object[]> rows = reportMarketPlaceRepository.avgPriceByCategory(fromTs, toPlusTs);

        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Object[] r : rows) {
            String catName = r[0] != null ? r[0].toString() : "UNKNOWN";
            BigDecimal avg = r[1] == null ? BigDecimal.ZERO : (BigDecimal) r[1];
            result.put(catName, avg.setScale(2, RoundingMode.HALF_UP));
        }
        return result;
    }

    //HELPER
    private Timestamp start(LocalDate date) {
        return Timestamp.valueOf(date.atStartOfDay());
    }

    private Timestamp end(LocalDate date) {
        return Timestamp.valueOf(date.plusDays(1).atStartOfDay());
    }
}
