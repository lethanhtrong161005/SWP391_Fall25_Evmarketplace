package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.services.statistics.transactionCounts;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.transaction.TransactionCountsDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TransactionCountsServiceImp implements TransactionCountsService{
    @Override
    public TransactionCountsDTO getTransactionCountsDto(LocalDate from, LocalDate to) {


        return null;
    }
}
