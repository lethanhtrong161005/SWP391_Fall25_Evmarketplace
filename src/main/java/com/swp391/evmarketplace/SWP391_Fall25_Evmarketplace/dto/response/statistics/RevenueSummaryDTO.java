package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.dto.response.statistics;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class RevenueSummaryDTO {

    private String from;          // yyyy-MM-dd hoặc null nếu lấy tất cả
    private String to;            // yyyy-MM-dd hoặc null
    private String currency;      // "VND"

    // ====== các chỉ số bạn yêu cầu ======
    private BigDecimal totalRevenue;                     // tổng doanh thu (PAID)
    private Map<String, BigDecimal> revenueBySource;     // phân loại nguồn thu: {"POST": x, "CONSIGNMENT": y, "OTHER": z}
    private BigDecimal averagePerDay;                    // doanh thu tb/ngày = totalRevenue / số ngày
    private BigDecimal averagePerPayingUser;             // doanh thu tb/người dùng trả tiền = totalRevenue / distinct payer_id (PAID)
    private BigDecimal averageTransactionValue;          // doanh thu tb mỗi giao dịch
    private double consignmentListingRevenueRate;        // tỉ lệ doanh thu ký gửi/ tổng doanh thu (0..1)
}
