package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@Data
public class TransactionCountsDTO {

    private String from;
    private String to;

    private long totalTransactions;                           // tổng giao dịch
    private Map<String, Long> transactionTypeBreakdown;       // phân loại: {"POST": x, "CONSIGNMENT": y, "OTHER": z}
    private long successfulTransactions;                      // tổng giao dịch thành công
    private double successRate;                               // tỷ lệ giao dịch thành công (0..1)
    private long failedOrCancelledTransactions;               // số lượng bị hủy/ thất bại
}
