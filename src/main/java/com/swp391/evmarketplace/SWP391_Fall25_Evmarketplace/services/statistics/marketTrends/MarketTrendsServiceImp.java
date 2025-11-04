package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.marketTrends;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.MarketTrendsDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.NameCount;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ReportMarketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

@Service
public class MarketTrendsServiceImp implements MarketTrendsService {

    @Autowired
    ReportMarketRepository repo;

    @Override
    public MarketTrendsDTO getMarketTrends(LocalDate from, LocalDate to, Integer topLimit) {
        boolean noRange = (from == null && to == null);
        if (noRange) {
            from = LocalDate.of(1970, 1, 1);
            to = LocalDate.of(2100, 1, 1);
        } else if (from == null) {
            from = to.minusDays(30);
        } else if (to == null) {
            to = LocalDate.now();
        }
        if (topLimit == null || topLimit <= 0) topLimit = 10;

        var postType = repo.countPostType(from, to);
        var byCategory = repo.countByCategory(from, to);

        var brandRows = repo.topBrands(from, to, topLimit);
        var modelRows = repo.topModels(from, to, topLimit);

        var topBrands = new ArrayList<NameCount>();
        for (Object[] r : brandRows) {
            topBrands.add(NameCount.builder()
                    .name(Objects.toString(r[0], "UNKNOWN"))
                    .count(((Number) r[1]).longValue())
                    .build());
        }

        var topModels = new ArrayList<NameCount>();
        for (Object[] r : modelRows) {
            topModels.add(NameCount.builder()
                    .name(Objects.toString(r[0], "UNKNOWN"))
                    .count(((Number) r[1]).longValue())
                    .build());
        }

        var avgPriceByCat = repo.avgPriceByCategory(from, to);

        return MarketTrendsDTO.builder()
                .from(noRange ? null : from.toString())
                .to(noRange ? null : to.toString())
                .currency("VND")

                .postTypeBreakdown(postType)
                .categoryBreakdown(byCategory)
                .topBrands(topBrands)
                .topModels(topModels)
                .avgListingPriceByCategory(avgPriceByCat)
                .build();
    }
}
