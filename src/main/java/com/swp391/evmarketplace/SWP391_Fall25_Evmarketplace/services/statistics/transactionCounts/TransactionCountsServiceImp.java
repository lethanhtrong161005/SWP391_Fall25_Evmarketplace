package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.transactionCounts;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.TransactionCountsDTO;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.ReportTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TransactionCountsServiceImp implements TransactionCountsService {
    @Autowired
    ReportTransactionRepository reportTransactionRepository;

    @Override
    public TransactionCountsDTO getTransactionCountsDto(LocalDate from, LocalDate to) {
        boolean noRange = (from == null && to == null);
        if (noRange) {
            from = LocalDate.of(1970, 1, 1);
            to = LocalDate.of(2100, 1, 1);
        } else if (from == null) from = to.minusDays(30);
        else if (to == null) to = LocalDate.now();

        long total = reportTransactionRepository.countTotalCreated(from, to);
        long success = reportTransactionRepository.countPaidSuccess(from, to);
        long failOrCancel = reportTransactionRepository.countFailedOrRefundedCreated(from, to);
        double successRate = total == 0 ? 0.0 : ((double) success / total);

        Map<String, Long> byPurpose = reportTransactionRepository.countByPurposeCreated(from, to);
        long post = byPurpose.getOrDefault("LISTING_FEE", 0L);
        long consignment = byPurpose.getOrDefault("ORDER", 0L);
        Long other = byPurpose.values().stream().mapToLong(Long::longValue).sum()
                - post - consignment;

        Map<String, Long> typeBreakdown = new LinkedHashMap<>();
        typeBreakdown.put("POST", post);
        typeBreakdown.put("CONSIGNMENT", consignment);
        typeBreakdown.put("OTHER", other);

        return TransactionCountsDTO.builder()
                .from(noRange ? null : from.toString())
                .to(noRange ? null : to.toString())
                .totalTransactions(total)
                .transactionTypeBreakdown(typeBreakdown)
                .successfulTransactions(success)
                .successRate(round(successRate, 3))
                .failedOrCancelledTransactions(failOrCancel)
                .build();
    }

    private double round(double v, int s) {
        return new java.math.BigDecimal(Double.toString(v))
                .setScale(s, java.math.RoundingMode.HALF_UP).doubleValue();
    }
}
