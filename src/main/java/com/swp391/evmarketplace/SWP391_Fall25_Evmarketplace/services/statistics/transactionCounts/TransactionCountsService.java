package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.transactionCounts;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics.TransactionCountsDTO;

import java.time.LocalDate;
import java.util.Map;

public interface TransactionCountsService {
    TransactionCountsDTO getTransactionCountsDto(LocalDate from, LocalDate to);

    long countTotalCreated(LocalDate from, LocalDate to);
    long countFailed(LocalDate from, LocalDate to);
    long countPaidSuccess(LocalDate from, LocalDate to);
    Map<String, Long> countByPurposeCreated(LocalDate from, LocalDate to);
    double successRate(LocalDate from , LocalDate to);

}
