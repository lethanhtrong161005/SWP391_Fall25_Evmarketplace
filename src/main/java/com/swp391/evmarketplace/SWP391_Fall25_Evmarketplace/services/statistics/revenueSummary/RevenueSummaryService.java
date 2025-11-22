package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.revenueSummary;


import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.RevenueSummaryDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface RevenueSummaryService {
    RevenueSummaryDTO getRevenueSummary(LocalDate from, LocalDate to);

    BigDecimal sumPaidAmount(LocalDate from, LocalDate to);
    long countDistinctPayingUsers(LocalDate from, LocalDate to);
    Map<String, BigDecimal> sumPaidAmountByPurpose(LocalDate from, LocalDate to);
    BigDecimal dayAvg(LocalDate from, LocalDate to);
    BigDecimal distinctPayers(LocalDate from, LocalDate to);
    BigDecimal avgTransactionsSuccess(LocalDate from, LocalDate to);
    double consignmentListingRevenueRate(LocalDate from, LocalDate to);
}
