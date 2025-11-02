package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.transaction;

import java.math.BigDecimal;
import java.util.Map;

public class TransactionCountsDTO {

    private String from;
    private String to;
    private String timezone;

    private long totalTransactions;                                  // tổng giao dịch
    private Map<String, Long> transactionTypeBreakdown;              // phân loại: {"POST": x, "CONSIGNMENT": y, "OTHER": z}
    private long successfulTransactions;                              // tổng giao dịch thành công
    private double successRate;                                       // tỷ lệ giao dịch thành công (0..1)
    private long failedOrCancelledTransactions;                       // số lượng bị hủy/ thất bại
    private long totalProductsSold;                                   // tổng số product được bán (chỉ tính consignment)
    private Map<String, Long> productsSoldBreakdown;                  // phân loại product bán được: {"EV": a, "BATTERY": b}
    private BigDecimal averageOrderValue;
}
