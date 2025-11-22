package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.transactionCounts;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.TransactionCountsDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ReportTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionCountsServiceImp implements TransactionCountsService {
    @Autowired
    private ReportTransactionRepository reportTransactionRepository;

    @Override
    public TransactionCountsDTO getTransactionCountsDto(LocalDate from, LocalDate to) {

        boolean noRange = (from == null && to == null);
        if (noRange) {
            from = LocalDate.of(1970, 1, 1);
            to   = LocalDate.of(2100, 1, 1);
        } else if (from == null) {
            from = to.minusDays(30);
        } else if (to == null) {
            to = LocalDate.now();
        }

        return TransactionCountsDTO.builder()
                .from(noRange ? null : from.toString())
                .to(noRange ? null : to.toString())
                .totalTransactions(countTotalCreated(from, to))
                .transactionType(countByPurposeCreated(from, to))
                .successfulTransactions(countPaidSuccess(from, to))
                .successRate(successRate(from, to))
                .failedOrCancelledTransactions(countFailed(from, to))
                .build();
    }

    @Override
    public long countTotalCreated(LocalDate from, LocalDate to) {
        Long result = reportTransactionRepository.countTotalCreated(start(from), end(to));
        return result;
    }

    @Override
    public long countFailed(LocalDate from, LocalDate to) {
        Long result = reportTransactionRepository.countFailedOrRefundedCreated(start(from), end(to));
        return result != null ? result : 0L;
    }

    @Override
    public long countPaidSuccess(LocalDate from, LocalDate to) {
        Long result = reportTransactionRepository.countPaidSuccess(start(from), end(to));
        return result != null ? result : 0L;
    }

    @Override
    public Map<String, Long> countByPurposeCreated(LocalDate from, LocalDate to) {
        List<Object[]> rows = reportTransactionRepository.countByPurposeCreated(start(from), end(to));

        long promotionCount = 0L;
        long orderCount = 0L;
        long otherCount = 0L;

        for (Object[] r : rows) {
            String purpose = r[0] != null ? r[0].toString() : "UNKNOWN";
            Long count = r[1] != null ? ((Number) r[1]).longValue() : 0L;

            switch (purpose) {
                case "PROMOTION": promotionCount += count; break;
                case "ORDER": orderCount += count; break;
                default: otherCount += count; break;
            }
        }

        Map<String, Long> map = new LinkedHashMap<>();
        map.put("PROMOTION", promotionCount);
        map.put("ORDER", orderCount);
//        map.put("OTHER", otherCount);

        return map;
    }

    @Override
    public double successRate(LocalDate from , LocalDate to) {
        Double total = (double) countTotalCreated(from, to);
        Double success = (double) countPaidSuccess(from, to);
        double result = total == 0 ? 0.0 : ((double) success/ total);
        return round(result, 3);
    }

    //HELPER
    private Timestamp start(LocalDate d) {
        return Timestamp.valueOf(d.atStartOfDay());
    }

    private Timestamp end(LocalDate d) {
        return Timestamp.valueOf(d.plusDays(1).atStartOfDay());
    }

    private double round(double v, int s) {
        return new java.math.BigDecimal(Double.toString(v))
                .setScale(s, java.math.RoundingMode.HALF_UP).doubleValue();
    }
}
