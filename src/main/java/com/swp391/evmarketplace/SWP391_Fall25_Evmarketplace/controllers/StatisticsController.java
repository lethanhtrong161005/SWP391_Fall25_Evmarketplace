package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.controllers;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.MarketTrendsDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.RevenueSummaryDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.TransactionCountsDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.marketTrends.MarketTrendsService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.revenueSummary.RevenueSummaryService;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.transactionCounts.TransactionCountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class StatisticsController {

    @Autowired
    TransactionCountsService transactionCountsService;

    @Autowired
    RevenueSummaryService revenueSummaryService;

    @Autowired
    MarketTrendsService getMarketTrends;

    @GetMapping("/transaction-counts")
    public TransactionCountsDTO getCounts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return transactionCountsService.getTransactionCountsDto(from, to);
    }

    @GetMapping("/revenue")
    public RevenueSummaryDTO getRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return revenueSummaryService.getRevenueSummary(from, to);
    }

    @GetMapping("/market")
    public MarketTrendsDTO getMarket(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false, defaultValue = "10") Integer topLimit
    ) {
        return getMarketTrends.getMarketTrends(from, to, topLimit);
    }

}
