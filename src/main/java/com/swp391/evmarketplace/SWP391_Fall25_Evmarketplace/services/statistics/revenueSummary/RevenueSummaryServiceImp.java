package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.revenueSummary;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.RevenueSummaryDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ReportRevenueRepository;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ReportTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RevenueSummaryServiceImp implements RevenueSummaryService {
    @Autowired
    private ReportRevenueRepository reportRevenueRepository;

    @Autowired
    private ReportTransactionRepository reportTransactionRepository;

    @Override
    public RevenueSummaryDTO getRevenueSummary(LocalDate from, LocalDate to) {

        boolean noRange = (from == null && to == null);
        if (noRange) {
            from = LocalDate.of(1970, 1, 1);
            to = LocalDate.of(2100, 1, 1);
        } else if (from == null) {
            from = to.minusDays(30);
        } else if (to == null) {
            to = LocalDate.now();
        }

        return RevenueSummaryDTO.builder()
                .from(noRange ? null : from.toString())
                .to(noRange ? null : to.toString())
                .currency("VND")

                .totalRevenue(sumPaidAmount(from, to))
                .revenueBySource(sumPaidAmountByPurpose(from, to))
                .averagePerDay(dayAvg(from, to))
                .averagePerPayingUser(distinctPayers(from, to))
                .averageTransactionValue(avgTransactionsSuccess(from, to))
                .consignmentListingRevenueRate(consignmentListingRevenueRate(from, to))
                .build();
    }

    @Override
    public BigDecimal sumPaidAmount(LocalDate from, LocalDate to) {
        Timestamp fromTs = start(from);
        Timestamp toPlusTs = end(to);

        BigDecimal result = reportRevenueRepository.sumPaidAmount(fromTs, toPlusTs);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public long countDistinctPayingUsers(LocalDate from, LocalDate to) {
        Timestamp fromTs = start(from);
        Timestamp toPlusTs = end(to);

        Long result = reportRevenueRepository.countDistinctPayingUsers(fromTs, toPlusTs);
        return result != null ? result : 0L;
    }

    @Override
    public Map<String, BigDecimal> sumPaidAmountByPurpose(LocalDate from, LocalDate to) {
        Timestamp fromTs = start(from);
        Timestamp toPlusTs = end(to);

        List<Object[]> rows = reportRevenueRepository.sumPaidAmountByPurpose(fromTs, toPlusTs);

        BigDecimal promotionSum = BigDecimal.ZERO;
        BigDecimal otherSum = BigDecimal.ZERO;


        for (Object[] r : rows) {
            String purpose = r[0] != null ? r[0].toString() : "UNKNOWN";
            BigDecimal sum = r[1] == null ? BigDecimal.ZERO : (BigDecimal) r[1];

            switch (purpose) {
                case "PROMOTION":
                    promotionSum = promotionSum.add(sum);
                    break;
                case "ORDER":
                    break;
                default:
                    otherSum = otherSum.add(sum);
                    break;
            }
        }

        BigDecimal orderSum = reportRevenueRepository.sumConsignmentCommission(fromTs, toPlusTs);
        if (orderSum == null) {
            orderSum = BigDecimal.ZERO;
        }

        Map<String, BigDecimal> result = new LinkedHashMap<>();
        result.put("PROMOTION", promotionSum);
        result.put("ORDER", orderSum);
//        result.put("OTHER", otherSum);
        return result;
    }

    @Override
    public BigDecimal dayAvg(LocalDate from, LocalDate to) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1; // [from, to] inclusive
        BigDecimal result = days <= 0
                ? BigDecimal.ZERO
                : sumPaidAmount(from, to).divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP);
        return result;
    }

    @Override
    public BigDecimal distinctPayers(LocalDate from, LocalDate to) {
        long distinctPayers = countDistinctPayingUsers(from, to);
        BigDecimal result = distinctPayers == 0
                ? BigDecimal.ZERO
                : sumPaidAmount(from, to).divide(BigDecimal.valueOf(distinctPayers), 2, RoundingMode.HALF_UP);
        return result;
    }

    @Override
    public BigDecimal avgTransactionsSuccess(LocalDate from, LocalDate to) {
        long transactionsSuccess = reportTransactionRepository.countPaidSuccess(start(from), end(to));
        BigDecimal result = transactionsSuccess == 0
                ? BigDecimal.ZERO
                : sumPaidAmount(from, to).divide(BigDecimal.valueOf(transactionsSuccess), 2, RoundingMode.HALF_UP);
        return result;
    }

    @Override
    public double consignmentListingRevenueRate(LocalDate from, LocalDate to) {
        Map<String, BigDecimal> byPurpose = sumPaidAmountByPurpose(from, to);
        BigDecimal consignment = byPurpose.getOrDefault("ORDER", BigDecimal.ZERO);
        double consignmentShare = sumPaidAmount(from, to).compareTo(BigDecimal.ZERO) == 0
                ? 0.0
                : consignment.divide(sumPaidAmount(from, to), 6, java.math.RoundingMode.HALF_UP).doubleValue();
        return consignmentShare;
    }


    //HELPER
    private Timestamp start(LocalDate date) {
        return Timestamp.valueOf(date.atStartOfDay());
    }

    private Timestamp end(LocalDate date) {
        return Timestamp.valueOf(date.plusDays(1).atStartOfDay());
    }

}
