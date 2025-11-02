package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.transactionCounts;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.transaction.TransactionCountsDTO;

import java.time.LocalDate;

public interface TransactionCountsService {
    TransactionCountsDTO getTransactionCountsDto(LocalDate from, LocalDate to);
}
